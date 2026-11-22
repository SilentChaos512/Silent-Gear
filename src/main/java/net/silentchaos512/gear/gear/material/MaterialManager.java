package net.silentchaos512.gear.gear.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.core.DataResourceManager;
import net.silentchaos512.gear.gear.MaterialJsonException;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MaterialManager extends DataResourceManager<Material> {
    private static final Collection<String> INGREDIENT_CONFLICT_LIST = new ArrayList<>();
    private final Multimap<String, Material> ingredientChecks = HashMultimap.create();
    private boolean needsSyncValidation = false;

    public MaterialManager() {
        super(
                MaterialSerializers.DISPATCH_CODEC,
                MaterialJsonException::new,
                "material",
                "silentgear_materials",
                "MaterialManager",
                SilentGear.LOGGER
        );
    }

    @Override
    public void validate(Material value, JsonObject json) {
        addIngredientChecks(value, json);
    }

    @Override
    public void onReloadPost() {
        checkForIngredientConflicts();
        this.ingredientChecks.clear();
        this.needsSyncValidation = true;
    }

    /**
     * Checks that every material can be sent to clients, removing any that cannot. Tags are not
     * available when materials are decoded, so problems like ingredients referencing tags that do
     * not exist would otherwise only show up as a vague decoding error on the client, which
     * disconnects the player. Must be called after tags are loaded. Only runs once per reload.
     *
     * @param registryAccess The server's registry access, with tags bound
     */
    public void validateForSync(RegistryAccess registryAccess) {
        if (!this.needsSyncValidation || isReloading()) return;
        this.needsSyncValidation = false;

        Map<Identifier, List<String>> problemsById = new LinkedHashMap<>();
        for (var entry : List.copyOf(entrySet())) {
            var problems = findSyncProblems(entry.getValue(), registryAccess);
            if (!problems.isEmpty()) {
                problemsById.put(entry.getKey(), problems);
            }
        }

        problemsById.forEach((id, problems) -> {
            SilentGear.LOGGER.error("Material \"{}\" (from pack \"{}\") cannot be sent to clients and has been removed:",
                    id, getPackName(get(id)));
            problems.forEach(problem -> SilentGear.LOGGER.error("  - {}", problem));
            removeInvalid(id);
        });

        if (!problemsById.isEmpty()) {
            GET_VALUES_CACHE_WITH_CHILDREN = List.of();
            GET_VALUES_CACHE_WITHOUT_CHILDREN = List.of();
        }
    }

    private static List<String> findSyncProblems(Material material, RegistryAccess registryAccess) {
        List<String> problems = new ArrayList<>();

        if (material instanceof AbstractMaterial abstractMaterial) {
            var crafting = abstractMaterial.getCraftingData();
            crafting.craftingItem().ifPresent(ingredient ->
                    checkIngredient("ingredient", ingredient, registryAccess, problems));
            crafting.partSubstitutes().forEach((partType, ingredient) ->
                    checkIngredient("part_substitutes." + SgRegistries.PART_TYPE.getKey(partType), ingredient, registryAccess, problems));
        }

        if (problems.isEmpty()) {
            // Catch anything else that would fail on the client
            try {
                simulateClientSync(MaterialSerializers.DISPATCH_STREAM_CODEC, material, registryAccess);
            } catch (Exception ex) {
                problems.add("Failed to encode or decode the material: " + ex);
            }
        }

        return problems;
    }

    private static void checkIngredient(String field, Ingredient ingredient, RegistryAccess registryAccess, List<String> problems) {
        try {
            simulateClientSync(Ingredient.CONTENTS_STREAM_CODEC, ingredient, registryAccess);
        } catch (Exception ex) {
            String ingredientJson = Ingredient.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, registryAccess), ingredient)
                    .result()
                    .map(JsonElement::toString)
                    .orElse("<unknown>");
            problems.add(String.format(
                    "\"%s\" is invalid: %s (%s). If this is a tag that might not exist, use "
                            + "{\"neoforge:ingredient_type\": \"silentgear:optional_tag\", \"tag\": \"#namespace:path\"} instead.",
                    field, ingredientJson, ex
            ));
        }
    }

    /**
     * Encodes a value with a stream codec, then decodes it again, the same way it would be sent
     * to and read by a client. Any exception the codec throws is passed on to the caller.
     */
    private static <T> void simulateClientSync(StreamCodec<RegistryFriendlyByteBuf, T> codec, T value, RegistryAccess registryAccess) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, ConnectionType.NEOFORGE);
        try {
            codec.encode(buf, value);
            codec.decode(buf);
        } finally {
            buf.release();
        }
    }

    private void addIngredientChecks(Material material, JsonObject json) {
        // Adds main ingredient to the map. Used to check for ingredient conflicts.
        JsonObject craftingItemsJson = json.getAsJsonObject("crafting_items");
        if (craftingItemsJson != null && craftingItemsJson.has("main")) {
            JsonElement mainJson = craftingItemsJson.get("main");
            String key = mainJson.toString();
            this.ingredientChecks.put(key, material);
        }
    }

    private void checkForIngredientConflicts() {
        INGREDIENT_CONFLICT_LIST.clear();

        for (String key : this.ingredientChecks.keySet()) {
            if (this.ingredientChecks.get(key).size() > 1) {
                String collect = this.ingredientChecks.get(key).stream().map(mat -> getKey(mat).toString()).collect(Collectors.joining(" and "));
                INGREDIENT_CONFLICT_LIST.add("Conflicting crafting items for: " + collect);
            }
        }
    }

    private static List<Material> GET_VALUES_CACHE_WITH_CHILDREN = List.of();
    private static List<Material> GET_VALUES_CACHE_WITHOUT_CHILDREN = List.of();

    public List<Material> getValues(boolean includeChildren) {
        if (isReloading()) {
            return List.of();
        }

        if (includeChildren) {
            if (isCacheInvalid(GET_VALUES_CACHE_WITH_CHILDREN)) {
                GET_VALUES_CACHE_WITH_CHILDREN = computeValuesList(true);
            }
            return GET_VALUES_CACHE_WITH_CHILDREN;
        } else {
            if (isCacheInvalid(GET_VALUES_CACHE_WITHOUT_CHILDREN)) {
                GET_VALUES_CACHE_WITHOUT_CHILDREN = computeValuesList(false);
            }
            return GET_VALUES_CACHE_WITHOUT_CHILDREN;
        }
    }

    private boolean isCacheInvalid(List<Material> list) {
        return list.isEmpty() || getKey(list.getFirst()) == null;
    }

    private List<Material> computeValuesList(boolean includeChildren) {
        synchronized (this) {
            ImmutableList.Builder<Material> builder = ImmutableList.builder();
            for (Material m : this) {
                if ((includeChildren || m.getParent() == null) && m.isValid()) {
                    builder.add(m);
                }
            }
            return builder.build();
        }
    }

    public List<Material> getChildren(Material material) {
        synchronized (this) {
            List<Material> list = new ArrayList<>();
            for (Material m : this) {
                if (m.getParent() == material) {
                    list.add(m);
                }
            }
            return list;
        }
    }

    public Optional<Material> getRandomObtainable(RandomSource randomSource) {
        synchronized (this) {
            var list = this.stream()
                    .filter(material -> material.getIngredient().isPresent())
                    .filter(material -> !material.isInCategory(MaterialCategories.INTANGIBLE))
                    .toList();
            return Util.getRandomSafe(list, randomSource);
        }
    }

    @Nullable
    public Material fromItem(ItemStack stack) {
        if (stack.isEmpty() || isReloading()) return null;

        var matches = new ArrayList<Material>();

        for (Material material : this) {
            var ingredient = material.getIngredient();
            if (ingredient.isPresent() && ingredient.get().test(stack)) {
                matches.add(material);
            }
        }

        // Try to find child materials before parents
        if (matches.size() > 1) {
            for (Material material : matches) {
                if (material.getParent() != null) {
                    return material;
                }
            }
        }

        // First match or null
        if (!matches.isEmpty()) {
            return matches.getFirst();
        }
        return null;
    }

    @Override
    public Collection<Component> getErrorMessages(ServerPlayer player) {
        Collection<Component> ret = super.getErrorMessages(player);
        INGREDIENT_CONFLICT_LIST.forEach(line -> {
            MutableComponent text = TextUtil.withColor(Component.literal(line), ChatFormatting.YELLOW);
            ret.add(Component.literal("[Silent Gear] ").append(text));
        });
        return ret;
    }
}

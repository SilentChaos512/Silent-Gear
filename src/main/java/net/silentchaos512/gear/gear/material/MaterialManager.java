package net.silentchaos512.gear.gear.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.core.DataResourceManager;
import net.silentchaos512.gear.gear.MaterialJsonException;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class MaterialManager extends DataResourceManager<Material> {
    private static final Collection<String> INGREDIENT_CONFLICT_LIST = new ArrayList<>();
    private final Multimap<String, Material> ingredientChecks = HashMultimap.create();

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
    public void validateAll() {
        checkForIngredientConflicts();
        this.ingredientChecks.clear();
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

    public List<Material> getValues(boolean includeChildren) {
        // TODO: Add a cache?
        synchronized (this) {
            List<Material> list = new ArrayList<>();
            for (Material m : this) {
                if (includeChildren || m.getParent() == null) {
                    list.add(m);
                }
            }
            return list;
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
                    .filter(material -> !material.getIngredient().isEmpty())
                    .filter(material -> !material.isInCategory(MaterialCategories.INTANGIBLE))
                    .toList();
            return Util.getRandomSafe(list, randomSource);
        }
    }

    @Nullable
    public Material fromItem(ItemStack stack) {
        if (stack.isEmpty()) return null;

        var matches = new ArrayList<Material>();

        for (Material material : this) {
            if (material.getIngredient().test(stack)) {
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

package net.silentchaos512.gear.gear.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
 import net.silentchaos512.gear.gear.MaterialJsonException;
import net.silentchaos512.gear.network.SyncMaterialsPacket;
import net.silentchaos512.gear.util.TextUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MaterialManager implements ResourceManagerReloadListener {
    public static final MaterialManager INSTANCE = new MaterialManager();

    private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();
    public static final Marker MARKER = MarkerManager.getMarker("MaterialManager");

    private static final String DATA_PATH = "silentgear_materials";
    private static final Map<ResourceLocation, IMaterial> MATERIALS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final List<IMaterial> ROOT_MATERIAL_LIST = new ArrayList<>();
    private static final Collection<String> ERROR_LIST = new ArrayList<>();
    private static final Collection<String> INGREDIENT_CONFLICT_LIST = new ArrayList<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DATA_PATH, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        Multimap<String, IMaterial> ingredientConflicts = HashMultimap.create();
        Collection<ResourceLocation> skippedList = new ArrayList<>();

        synchronized (MATERIALS) {
            MATERIALS.clear();
            ERROR_LIST.clear();
            SilentGear.LOGGER.info(MARKER, "Reloading material files");

            String packName = "ERROR";
            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource iresource = resourceOptional.get();
                    packName = iresource.sourcePackId();
                    JsonObject json = null;
                    try {
                        json = GsonHelper.fromJson(GSON, IOUtils.toString(iresource.open(), StandardCharsets.UTF_8), JsonObject.class);
                    } catch (IOException ex) {
                        SilentGear.LOGGER.error(MARKER, "Could not read material {}", name, ex);
                        ERROR_LIST.add(String.format("%s (%s)", name, packName));
                    }

                    if (json == null) {
                        // Something is very wrong or the JSON is somehow empty
                        SilentGear.LOGGER.error(MARKER, "Could not load material {} as it's null or empty", name);
                    } else {
                        // Attempt to deserialize the material
                        IMaterial material = tryDeserialize(name, packName, json);
                        MATERIALS.put(material.getId(), material);
                        addIngredientChecks(ingredientConflicts, material, json);
                    }
                }
            }
            recreateMaterialCache();
        }

        checkForIngredientConflicts(ingredientConflicts);
        logSkippedMaterials(skippedList);
    }

    private static IMaterial tryDeserialize(ResourceLocation name, String packName, JsonObject json) {
        SilentGear.LOGGER.info("Deserializing material {} in pack {}", name, packName);
        try {
            return MaterialSerializers.deserialize(name, packName, json);
        } catch (JsonSyntaxException ex) {
            throw new MaterialJsonException(name, packName, ex);
        }
    }

    private static void addIngredientChecks(Multimap<String, IMaterial> map, IMaterial material, JsonObject json) {
        // Adds main ingredient to the map. Used to check for ingredient conflicts.
        JsonObject craftingItemsJson = json.getAsJsonObject("crafting_items");
        if (craftingItemsJson != null && craftingItemsJson.has("main")) {
            JsonElement mainJson = craftingItemsJson.get("main");
            String key = GSON.toJson(mainJson);
            map.put(key, material);
        }
    }

    private static void checkForIngredientConflicts(Multimap<String, IMaterial> map) {
        INGREDIENT_CONFLICT_LIST.clear();

        for (String key : map.keySet()) {
            if (map.get(key).size() > 1) {
                String collect = map.get(key).stream().map(mat -> mat.getId().toString()).collect(Collectors.joining(" and "));
                INGREDIENT_CONFLICT_LIST.add("Conflicting crafting items for: " + collect);
            }
        }
    }

    private static void logSkippedMaterials(Collection<ResourceLocation> list) {
        if (!list.isEmpty()) {
            SilentGear.LOGGER.info("Skipped loading {} material(s), as their conditions were not met. This is usually NOT an error!",
                    list.size());
            SilentGear.LOGGER.info("Skipped materials: {}",
                    list.stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")));
        }
    }

    public static Collection<IMaterial> getValues() {
        synchronized (MATERIALS) {
            return MATERIALS.values();
        }
    }

    public static Collection<IMaterial> getValues(boolean includeChildren) {
        if (includeChildren) {
            return getValues();
        }
        return Collections.unmodifiableList(ROOT_MATERIAL_LIST);
    }

    public static List<IMaterial> getChildren(IMaterial material) {
        synchronized (MATERIALS) {
            List<IMaterial> list = new ArrayList<>();
            for (IMaterial m : MATERIALS.values()) {
                if (m.getParent() == material) {
                    list.add(m);
                }
            }
            return list;
        }
    }

    @Nullable
    public static IMaterial get(@Nullable ResourceLocation id) {
        if (id == null) return null;

        synchronized (MATERIALS) {
            return MATERIALS.get(id);
        }
    }

    @Nullable
    public static IMaterial from(ItemStack stack) {
        if (stack.isEmpty()) return null;

        for (IMaterial material : getValues()) {
            if (material.getIngredient().test(stack)) {
                return material;
            }
        }

        return null;
    }

    public static void handleSyncPacket(SyncMaterialsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        synchronized (MATERIALS) {
            Map<ResourceLocation, IMaterial> oldMaterials = ImmutableMap.copyOf(MATERIALS);
            MATERIALS.clear();
            for (IMaterial mat : msg.getMaterials()) {
                mat.retainData(oldMaterials.get(mat.getId()));
                MATERIALS.put(mat.getId(), mat);
            }
            recreateMaterialCache();
            SilentGear.LOGGER.info("Read {} materials from server", MATERIALS.size());
        }
        ctx.get().setPacketHandled(true);
    }

    public static Collection<Component> getErrorMessages(ServerPlayer player) {
        Collection<Component> ret = new ArrayList<>();
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            ret.add(TextUtil.withColor(Component.literal("[Silent Gear] The following materials failed to load, check your log file:"),
                    ChatFormatting.RED));
            ret.add(Component.literal(listStr));
        }
        INGREDIENT_CONFLICT_LIST.forEach(line -> {
            MutableComponent text = TextUtil.withColor(Component.literal(line), ChatFormatting.YELLOW);
            ret.add(Component.literal("[Silent Gear] ").append(text));
        });
        return ret;
    }

    private static void recreateMaterialCache() {
        synchronized (MATERIALS) {
            ROOT_MATERIAL_LIST.clear();
            for (IMaterial mat : MATERIALS.values()) {
                if (mat.getParent() == null) {
                    ROOT_MATERIAL_LIST.add(mat);
                }
            }
        }
    }
}

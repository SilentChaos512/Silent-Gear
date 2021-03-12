package net.silentchaos512.gear.gear.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
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
public class MaterialManager implements IResourceManagerReloadListener {
    public static final MaterialManager INSTANCE = new MaterialManager();

    private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();
    public static final Marker MARKER = MarkerManager.getMarker("MaterialManager");

    private static final String DATA_PATH = "silentgear_materials";
    private static final Map<ResourceLocation, IMaterial> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();
    private static final Collection<String> INGREDIENT_CONFLICT_LIST = new ArrayList<>();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(DATA_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        Multimap<String, IMaterial> ingredientConflicts = HashMultimap.create();
        Collection<ResourceLocation> skippedList = new ArrayList<>();

        synchronized (MAP) {
            MAP.clear();
            ERROR_LIST.clear();
            SilentGear.LOGGER.info(MARKER, "Reloading material files");

            for (ResourceLocation id : resources) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                String packName = "ERROR";
                try (IResource iresource = resourceManager.getResource(id)) {
                    packName = iresource.getPackName();
                    JsonObject json = JSONUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (json == null) {
                        // Something is very wrong or the JSON is somehow empty
                        SilentGear.LOGGER.error(MARKER, "Could not load material {} as it's null or empty", name);
                    } else if (!CraftingHelper.processConditions(json, "conditions")) {
                        // Conditions not met, so do not load the material
                        skippedList.add(name);
                    } else {
                        // Attempt to deserialize the material
                        IMaterial material = MaterialSerializers.deserialize(name, packName, json);
                        MAP.put(material.getId(), material);
                        addIngredientChecks(ingredientConflicts, material, json);
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    SilentGear.LOGGER.error(MARKER, "Parsing error loading material {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                } catch (IOException ex) {
                    SilentGear.LOGGER.error(MARKER, "Could not read material {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                }
            }
        }

        checkForIngredientConflicts(ingredientConflicts);
        logSkippedMaterials(skippedList);
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

    public static List<IMaterial> getValues() {
        return getValues(true);
    }

    public static List<IMaterial> getValues(boolean includeChildren) {
        synchronized (MAP) {
            List<IMaterial> list = new ArrayList<>();
            for (IMaterial m : MAP.values()) {
                if (includeChildren || m.getParent() == null) {
                    list.add(m);
                }
            }
            return list;
        }
    }

    public static List<IMaterial> getChildren(IMaterial material) {
        synchronized (MAP) {
            List<IMaterial> list = new ArrayList<>();
            for (IMaterial m : MAP.values()) {
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

        synchronized (MAP) {
            return MAP.get(id);
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
        synchronized (MAP) {
            Map<ResourceLocation, IMaterial> oldMaterials = ImmutableMap.copyOf(MAP);
            MAP.clear();
            for (IMaterial mat : msg.getMaterials()) {
                mat.retainData(oldMaterials.get(mat.getId()));
                MAP.put(mat.getId(), mat);
            }
            SilentGear.LOGGER.info("Read {} materials from server", MAP.size());
        }
        ctx.get().setPacketHandled(true);
    }

    public static Collection<ITextComponent> getErrorMessages(ServerPlayerEntity player) {
        Collection<ITextComponent> ret = new ArrayList<>();
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            ret.add(TextUtil.withColor(new StringTextComponent("[Silent Gear] The following materials failed to load, check your log file:"),
                    TextFormatting.RED));
            ret.add(new StringTextComponent(listStr));
        }
        INGREDIENT_CONFLICT_LIST.forEach(line -> {
            IFormattableTextComponent text = TextUtil.withColor(new StringTextComponent(line), TextFormatting.YELLOW);
            ret.add(new StringTextComponent("[Silent Gear] ").append(text));
        });
        return ret;
    }
}

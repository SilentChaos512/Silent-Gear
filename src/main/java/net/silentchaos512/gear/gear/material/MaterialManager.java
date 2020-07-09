package net.silentchaos512.gear.gear.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.network.SyncMaterialsPacket;
import net.silentchaos512.gear.parts.PartConst;
import net.silentchaos512.gear.parts.PartManager;
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

    public static final Marker MARKER = MarkerManager.getMarker("MaterialManager");

    private static final String DATA_PATH = "silentgear_materials";
    private static final Map<ResourceLocation, IMaterial> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(DATA_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

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
                    JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (json == null) {
                        SilentGear.LOGGER.error(MARKER, "Could not load material {} as it's null or empty", name);
                    } else if (!CraftingHelper.processConditions(json, "conditions")) {
                        SilentGear.LOGGER.info(MARKER, "Skipping loading material {} as its conditions were not met", name);
                    } else {
                        IMaterial material = MaterialSerializers.deserialize(name, packName, json);
                        MAP.put(material.getId(), material);
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    SilentGear.LOGGER.error(MARKER, "Parsing error loading material {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                } catch (IOException ex) {
                    SilentGear.LOGGER.error(MARKER, "Could not read material {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                }
            }

            createAdapterMaterials();
        }
    }

    private static void createAdapterMaterials() {
        // Create adapter materials for temporary compatibility with older data packs
        // Remove in 1.16!

        SilentGear.LOGGER.info(MARKER, "Trying to create adapter materials...");

        for (IGearPart part : PartManager.getValues()) {
            if (isBuiltInPart(part)) {
                SilentGear.LOGGER.debug(MARKER, "Not creating adapter for '{}': built-in part", part.getId());
            } else {
                String[] pathParts = part.getId().getPath().split("/");

                if (pathParts.length != 2) {
                    SilentGear.LOGGER.debug(MARKER, "Not considering '{}' for conversion, non-standard name format", part.getId());
                } else if (MAP.containsKey(SilentGear.getId(pathParts[1]))) {
                    SilentGear.LOGGER.debug(MARKER, "Not creating adapter for '{}': has like-named material already", part.getId());
                } else {
                    PartType partType = PartType.get(SilentGear.getId(pathParts[0]));
                    ResourceLocation materialId = new ResourceLocation(part.getId().getNamespace(), pathParts[1]);

                    if (partType != null) {
                        IMaterial material = MAP.computeIfAbsent(materialId, SimplePartAdapterMaterial::new);

                        if (material instanceof SimplePartAdapterMaterial) {
                            ((SimplePartAdapterMaterial) material).addPart(part);
                            SilentGear.LOGGER.info(MARKER, "Added part '{}' to adapter material '{}'", part.getId(), materialId);
                        } else {
                            SilentGear.LOGGER.debug(MARKER, "Not creating adapter for '{}': material already defined", materialId);
                        }
                    }
                }
            }
        }
    }

    private static boolean isBuiltInPart(IGearPart part) {
        IGearPart example = PartManager.get(PartConst.MAIN_EXAMPLE);
        return example == null || part.getPackName().equals(example.getPackName());
    }

    public static List<IMaterial> getValues() {
        return getValues(true);
    }

    public static List<IMaterial> getValues(boolean includeChildren) {
        synchronized (MAP) {
            return MAP.values().stream()
                    .filter(m -> includeChildren || m.getParent() == null)
                    .collect(Collectors.toList());
        }
    }

    public static List<IMaterial> getChildren(IMaterial material) {
        synchronized (MAP) {
            return MAP.values().stream()
                    .filter(m -> m.getParent() == material)
                    .collect(Collectors.toList());
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
            if (material.getIngredient(PartType.MAIN).test(stack)) {
                return material;
            }
        }

        return null;
    }

    public static void handleSyncPacket(SyncMaterialsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        synchronized (MAP) {
            Map<ResourceLocation, IMaterial> oldMaterials = ImmutableMap.copyOf(MAP);
            MAP.clear();
            msg.getMaterials().forEach(mat -> {
                mat.retainData(oldMaterials.get(mat.getId()));
                MAP.put(mat.getId(), mat);
            });
            SilentGear.LOGGER.info("Read {} materials from server", MAP.size());
        }
        ctx.get().setPacketHandled(true);
    }

    public static Collection<ITextComponent> getErrorMessages(ServerPlayerEntity player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            return ImmutableList.of(
                    new StringTextComponent("[Silent Gear] The following materials failed to load, check your log file:")
                            .applyTextStyle(TextFormatting.RED),
                    new StringTextComponent(listStr)
            );
        }
        return ImmutableList.of();
    }
}

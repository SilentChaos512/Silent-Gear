package net.silentchaos512.gear.client.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartDisplay;
import net.silentchaos512.gear.api.part.PartDisplay;
import net.silentchaos512.gear.client.model.fragment.FragmentModelLoader;
import net.silentchaos512.gear.client.model.gear.GearModelLoader;
import net.silentchaos512.gear.client.model.part.CompoundPartModelLoader;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.IEarlySelectiveReloadListener;
import net.silentchaos512.gear.util.TextUtil;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class GearDisplayManager implements IEarlySelectiveReloadListener {
    public static final GearDisplayManager INSTANCE = new GearDisplayManager();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String PATH_MATERIALS = "silentgear_materials";
    private static final String PATH_PARTS = "silentgear_parts";
    private static final Map<ResourceLocation, IMaterialDisplay> MATERIALS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<ResourceLocation, IPartDisplay> PARTS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    private GearDisplayManager() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        CompoundPartModelLoader.clearCaches();
        FragmentModelLoader.clearCaches();
        GearModelLoader.clearCaches();

        ERROR_LIST.clear();
        reloadMaterials(resourceManager);
        reloadParts(resourceManager);
    }

    private static void reloadMaterials(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(PATH_MATERIALS, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (MATERIALS) {
            SilentGear.LOGGER.info("Reloading material model files");
            MATERIALS.clear();

            String packName = "ERROR";
            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(PATH_MATERIALS.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource iresource = resourceOptional.get();
                    packName = iresource.sourcePackId();
                    JsonObject json = null;
                    try {
                        json = GsonHelper.fromJson(GSON, IOUtils.toString(iresource.open(), StandardCharsets.UTF_8), JsonObject.class);
                    } catch (IOException ex) {
                        SilentGear.LOGGER.error("Could not read material model {}", name, ex);
                        ERROR_LIST.add(String.format("material:%s (%s)", name, packName));
                    }

                    if (json == null) {
                        SilentGear.LOGGER.error("Could not load material model {} as it's null or empty", name);
                    } else {
                        IMaterialDisplay model = MaterialDisplay.deserialize(name, json);
                        MATERIALS.put(name, model);
                    }
                }
            }
        }
    }

    private static void reloadParts(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(PATH_PARTS, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (PARTS) {
            SilentGear.LOGGER.info("Reloading part model files");
            PARTS.clear();

            String packName = "ERROR";
            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(PATH_PARTS.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource iresource = resourceOptional.get();
                    packName = iresource.sourcePackId();
                    JsonObject json = null;
                    try {
                        json = GsonHelper.fromJson(GSON, IOUtils.toString(iresource.open(), StandardCharsets.UTF_8), JsonObject.class);
                    } catch (IOException ex) {
                        SilentGear.LOGGER.error("Could not read part model {}", name, ex);
                        ERROR_LIST.add(String.format("part:%s (%s)", name, packName));
                    }

                    if (json == null) {
                        SilentGear.LOGGER.error("Could not load part model {} as it's null or empty", name);
                    } else {
                        IPartDisplay model = PartDisplay.deserialize(name, json);
                        PARTS.put(name, model);
                    }
                }
            }
        }
    }

    public static Collection<IMaterialDisplay> getMaterials() {
        synchronized (MATERIALS) {
            Collection<IMaterialDisplay> ret = new ArrayList<>();
            for (IMaterial material : MaterialManager.getValues()) {
                MaterialInstance mat = MaterialInstance.of(material);
                ret.add(mat.getDisplayProperties());
            }
            ret.addAll(MATERIALS.values());
            return ret;
        }
    }

    public static Collection<IPartDisplay> getParts() {
        synchronized (PARTS) {
            return PARTS.values();
        }
    }

    /**
     * Gets the materials model, or a default model if none was loaded for the material.
     *
     * @param material The material
     * @return A material model (possibly a default one)
     */
    @Nullable
    public static IMaterialDisplay get(IMaterialInstance material) {
        IMaterial mat = material.get();
        if (mat != null) {
            IMaterialDisplay displayOverride = mat.getDisplayOverride(material);
            if (displayOverride != null) {
                return displayOverride;
            }
            return get(mat);
        }
        return getMaterial(material.getId());
    }

    /**
     * @param material The material
     * @return The material's model
     * @deprecated Use {@link #get(IMaterialInstance)} instead
     */
    @Deprecated
    @Nullable
    public static IMaterialDisplay get(IMaterial material) {
        return getMaterial(material.getId());
    }

    /**
     * @param materialId The material ID
     * @return The material's model
     * @deprecated Internal use only, use {@link #get(IMaterialInstance)} instead
     */
    @Deprecated
    @Nullable
    public static IMaterialDisplay getMaterial(ResourceLocation materialId) {
        synchronized (MATERIALS) {
            return MATERIALS.get(materialId);
        }
    }

    @Nullable
    public static IPartDisplay get(IGearPart part) {
        return getPart(part.getId());
    }

    @Nullable
    public static IPartDisplay getPart(ResourceLocation partId) {
        synchronized (PARTS) {
            return PARTS.get(partId);
        }
    }

    public static Collection<Component> getErrorMessages(Player player) {
        Collection<Component> ret = new ArrayList<>();
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            ret.add(TextUtil.withColor(Component.literal("[Silent Gear] The following part/material models failed to load, check your log file:"),
                    ChatFormatting.RED));
            ret.add(Component.literal(listStr));
        }
        return ret;
    }
}

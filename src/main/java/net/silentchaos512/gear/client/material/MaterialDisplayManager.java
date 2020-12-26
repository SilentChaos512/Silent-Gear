package net.silentchaos512.gear.client.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartDisplay;
import net.silentchaos512.gear.api.part.PartDisplay;
import net.silentchaos512.gear.client.model.fragment.FragmentModelLoader;
import net.silentchaos512.gear.client.model.gear.GearModelLoader;
import net.silentchaos512.gear.client.model.part.CompoundPartModelLoader;
import net.silentchaos512.gear.util.IEarlySelectiveReloadListener;
import net.silentchaos512.gear.util.TextUtil;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

// TODO: Rename to GearDisplayManager?
public final class MaterialDisplayManager implements IEarlySelectiveReloadListener {
    public static final MaterialDisplayManager INSTANCE = new MaterialDisplayManager();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String PATH_MATERIALS = "silentgear_materials";
    private static final String PATH_PARTS = "silentgear_parts";
    private static final Map<ResourceLocation, IMaterialDisplay> MATERIALS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<ResourceLocation, IPartDisplay> PARTS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    private MaterialDisplayManager() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> predicate) {
        if (predicate.test(VanillaResourceType.MODELS)) {
            CompoundPartModelLoader.clearCaches();
            FragmentModelLoader.clearCaches();
            GearModelLoader.clearCaches();

            ERROR_LIST.clear();
            reloadMaterials(resourceManager);
            reloadParts(resourceManager);
        }
    }

    private static void reloadMaterials(IResourceManager resourceManager) {
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(PATH_MATERIALS, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (MATERIALS) {
            SilentGear.LOGGER.info("Reloading material model files");
            MATERIALS.clear();

            String packName = "ERROR";
            for (ResourceLocation id : resources) {
                String path = id.getPath().substring(PATH_MATERIALS.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                try (IResource iresource = resourceManager.getResource(id)) {
                    packName = iresource.getPackName();
                    JsonObject json = JSONUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);

                    if (json == null) {
                        SilentGear.LOGGER.error("Could not load material model {} as it's null or empty", name);
                    } else {
                        IMaterialDisplay model = MaterialDisplay.deserialize(name, json);
                        MATERIALS.put(name, model);
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    SilentGear.LOGGER.error("Parsing error loading material model {}", name, ex);
                    ERROR_LIST.add(String.format("material:%s (%s)", name, packName));
                } catch (IOException ex) {
                    SilentGear.LOGGER.error("Could not read material model {}", name, ex);
                    ERROR_LIST.add(String.format("material:%s (%s)", name, packName));
                }
            }
        }
    }

    private static void reloadParts(IResourceManager resourceManager) {
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(PATH_PARTS, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (PARTS) {
            SilentGear.LOGGER.info("Reloading part model files");
            PARTS.clear();

            String packName = "ERROR";
            for (ResourceLocation id : resources) {
                String path = id.getPath().substring(PATH_PARTS.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                try (IResource iresource = resourceManager.getResource(id)) {
                    packName = iresource.getPackName();
                    JsonObject json = JSONUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);

                    if (json == null) {
                        SilentGear.LOGGER.error("Could not load part model {} as it's null or empty", name);
                    } else {
                        IPartDisplay model = PartDisplay.deserialize(name, json);
                        PARTS.put(name, model);
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    SilentGear.LOGGER.error("Parsing error loading part model {}", name, ex);
                    ERROR_LIST.add(String.format("part:%s (%s)", name, packName));
                } catch (IOException ex) {
                    SilentGear.LOGGER.error("Could not read part model {}", name, ex);
                    ERROR_LIST.add(String.format("part:%s (%s)", name, packName));
                }
            }
        }
    }

    public static Collection<IMaterialDisplay> getMaterials() {
        synchronized (MATERIALS) {
            return MATERIALS.values();
        }
    }

    public static Collection<IPartDisplay> getParts() {
        synchronized (PARTS) {
            return PARTS.values();
        }
    }

    public static IMaterialDisplay get(IMaterial material) {
        if (!material.isSimple()) {
            return CompoundMaterialDisplay.INSTANCE;
        }

        return getMaterial(material.getId());
    }

    public static IMaterialDisplay getMaterial(ResourceLocation materialId) {
        synchronized (MATERIALS) {
            return MATERIALS.getOrDefault(materialId, DefaultMaterialDisplay.INSTANCE);
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

    public static Collection<ITextComponent> getErrorMessages(PlayerEntity player) {
        Collection<ITextComponent> ret = new ArrayList<>();
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            ret.add(TextUtil.withColor(new StringTextComponent("[Silent Gear] The following part/material models failed to load, check your log file:"),
                    TextFormatting.RED));
            ret.add(new StringTextComponent(listStr));
        }
        return ret;
    }
}

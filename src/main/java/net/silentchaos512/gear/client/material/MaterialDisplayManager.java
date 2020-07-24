package net.silentchaos512.gear.client.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.client.model.GearModelLoader;
import net.silentchaos512.gear.util.IEarlySelectiveReloadListener;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public final class MaterialDisplayManager implements IEarlySelectiveReloadListener {
    public static final MaterialDisplayManager INSTANCE = new MaterialDisplayManager();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String ASSETS_PATH = "silentgear_materials";
    private static final Map<ResourceLocation, IMaterialDisplay> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    private MaterialDisplayManager() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> predicate) {
        if (predicate.test(VanillaResourceType.MODELS)) {
            Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(ASSETS_PATH, s -> s.endsWith(".json"));
            if (resources.isEmpty()) return;

            synchronized (MAP) {
                SilentGear.LOGGER.info("Reloading material display files");
                MAP.clear();
                ERROR_LIST.clear();

                GearModelLoader.clearCaches();

                String packName = "ERROR";
                for (ResourceLocation id : resources) {
                    String path = id.getPath().substring(ASSETS_PATH.length() + 1, id.getPath().length() - ".json".length());
                    ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                    try (IResource iresource = resourceManager.getResource(id)) {
                        packName = iresource.getPackName();
                        JsonObject json = JSONUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);

                        if (json == null) {
                            SilentGear.LOGGER.error("Could not load material model {} as it's null or empty", name);
                        } else {
                            IMaterialDisplay model = MaterialDisplay.deserialize(json);
                            MAP.put(name, model);
                        }
                    } catch (IllegalArgumentException | JsonParseException ex) {
                        SilentGear.LOGGER.error("Parsing error loading material model {}", name, ex);
                        ERROR_LIST.add(String.format("%s (%s)", name, packName));
                    } catch (IOException ex) {
                        SilentGear.LOGGER.error("Could not read material model {}", name, ex);
                        ERROR_LIST.add(String.format("%s (%s)", name, packName));
                    }
                }
            }
        }
    }

    public static Collection<IMaterialDisplay> getValues() {
        synchronized (MAP) {
            return MAP.values();
        }
    }

    @Nullable
    public static IMaterialDisplay get(IMaterial material) {
        return get(material.getId());
    }

    @Nullable
    public static IMaterialDisplay get(ResourceLocation materialId) {
        synchronized (MAP) {
            return MAP.get(materialId);
        }
    }
}

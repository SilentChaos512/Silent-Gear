package net.silentchaos512.gear.compat.gamestages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IGearPart;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * Do not use directly, use {@link GameStagesCompatProxy} instead.
 */
public class GameStagesCompat implements ISelectiveResourceReloadListener {
    public static final IFutureReloadListener INSTANCE = new GameStagesCompat();

    private static final Map<ResourceLocation, List<String>> PARTS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, List<String>> GEAR_TYPES = Collections.synchronizedMap(new HashMap<>());

    static boolean canCraft(IGearPart part, PlayerEntity player) {
        synchronized (PARTS) {
            List<String> stages = PARTS.getOrDefault(part.getId(), Collections.emptyList());
            return hasAnyStage(player, stages);
        }
    }

    static boolean canCraft(GearType gearType, PlayerEntity player) {
        synchronized (GEAR_TYPES) {
            List<String> stages = GEAR_TYPES.getOrDefault(gearType.getName(), Collections.emptyList());
            return hasAnyStage(player, stages);
        }
    }

    private static boolean hasAnyStage(PlayerEntity player, Collection<String> stages) {
        if (stages.isEmpty()) {
            return true;
        }

        IStageData data = GameStageHelper.getPlayerData(player);
        if (data == null) {
            return true;
        }

        for (String stage : stages) {
            if (data.hasStage(stage)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations("silentgear_gamestages", s -> s.endsWith(".json"));

        for (ResourceLocation id : resources) {
            String path = id.getPath().substring("silentgear_gamestages".length() + 1, id.getPath().length() - ".json".length());
            ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
            if (name.equals(SilentGear.getId("parts"))) {
                synchronized (PARTS) {
                    try (IResource iresource = resourceManager.getResource(id)) {
                        JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                        if (json != null) {
                            PARTS.clear();
                            json.entrySet().forEach(entry -> PARTS.put(new ResourceLocation(entry.getKey()), parseStages(entry.getValue())));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (name.equals(SilentGear.getId("gear_types"))) {
                synchronized (GEAR_TYPES) {
                    try (IResource iresource = resourceManager.getResource(id)) {
                        JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                        if (json != null) {
                            GEAR_TYPES.clear();
                            json.entrySet().forEach(entry -> GEAR_TYPES.put(entry.getKey(), parseStages(entry.getValue())));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                SilentGear.LOGGER.info("Unknown game stages JSON file found: {}", id);
            }
        }
    }

    private static List<String> parseStages(JsonElement json) {
        if (json.isJsonArray()) {
            List<String> list = new ArrayList<>();
            json.getAsJsonArray().forEach(e -> list.add(e.getAsString()));
            return list;
        }
        return Collections.singletonList(json.getAsString());
    }
}

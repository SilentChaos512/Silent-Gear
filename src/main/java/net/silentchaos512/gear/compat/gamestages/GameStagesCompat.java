package net.silentchaos512.gear.compat.gamestages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import net.darkhax.gamestage.GameStageHelper;
//import net.darkhax.gamestages.data.IStageData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Do not use directly, use {@link GameStagesCompatProxy} instead.
 */
@SuppressWarnings("deprecation")
public class GameStagesCompat implements ResourceManagerReloadListener {
    public static final PreparableReloadListener INSTANCE = new GameStagesCompat();

    private static final Map<ResourceLocation, List<String>> PARTS = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, List<String>> GEAR_TYPES = Collections.synchronizedMap(new HashMap<>());

    static boolean canCraft(IGearPart part, Player player) {
        synchronized (PARTS) {
            List<String> stages = PARTS.getOrDefault(part.getId(), Collections.emptyList());
            return hasAnyStage(player, stages);
        }
    }

    static boolean canCraft(GearType gearType, Player player) {
        synchronized (GEAR_TYPES) {
            List<String> stages = GEAR_TYPES.getOrDefault(gearType.getName(), Collections.emptyList());
            return hasAnyStage(player, stages);
        }
    }

    private static boolean hasAnyStage(Player player, Collection<String> stages) {
        /*if (stages.isEmpty()) {
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
        }*/

        return false;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.listResources("silentgear_gamestages", s -> s.endsWith(".json"));

        for (ResourceLocation id : resources) {
            String path = id.getPath().substring("silentgear_gamestages".length() + 1, id.getPath().length() - ".json".length());
            ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
            if (name.equals(SilentGear.getId("parts"))) {
                synchronized (PARTS) {
                    try (Resource iresource = resourceManager.getResource(id)) {
                        JsonObject json = GsonHelper.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
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
                    try (Resource iresource = resourceManager.getResource(id)) {
                        JsonObject json = GsonHelper.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
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

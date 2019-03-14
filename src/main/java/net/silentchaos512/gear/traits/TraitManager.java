package net.silentchaos512.gear.traits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.network.SyncTraitsPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class TraitManager implements IResourceManagerReloadListener {
    public static final TraitManager INSTANCE = new TraitManager();

    public static final Marker MARKER = MarkerManager.getMarker("TraitManager");

    private static final String DATA_PATH = "silentgear/traits/";
    private static final Map<ResourceLocation, ITrait> MAP = new LinkedHashMap<>();

    private TraitManager() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(
                DATA_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        MAP.clear();
        SilentGear.LOGGER.info(MARKER, "Reloading trait files");

        for (ResourceLocation id : resources) {
            try (IResource iresource = resourceManager.getResource(id)) {
                String path = id.getPath().substring(DATA_PATH.length(), id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
                SilentGear.LOGGER.debug(MARKER, "Found likely trait file: {}, trying to read as trait {}", id, name);

                JsonObject json = JsonUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                if (json == null) {
                    SilentGear.LOGGER.error(MARKER, "could not load trait {} as it's null or empty", name);
                } else {
                    addTrait(TraitSerializers.deserialize(name, json));
                }
            } catch (IllegalArgumentException | JsonParseException ex) {
                SilentGear.LOGGER.error(MARKER, "Parsing error loading trait {}", id, ex);
            } catch (IOException ex) {
                SilentGear.LOGGER.error(MARKER, "Could not read trait {}", id, ex);
            }
        }

        SilentGear.LOGGER.info(MARKER, "Finished! Registered {} traits", MAP.size());
    }

    private static void addTrait(ITrait trait) {
        if (MAP.containsKey(trait.getId())) {
            throw new IllegalArgumentException("Duplicate trait " + trait.getId());
        } else {
            MAP.put(trait.getId(), trait);
        }
    }

    public static Collection<ITrait> getValues() {
        return MAP.values();
    }

    @Nullable
    public static ITrait get(ResourceLocation id) {
        return MAP.get(id);
    }

    @Nullable
    public static ITrait get(String strId) {
        return get(new ResourceLocation(strId));
    }

    public static void handleTraitSyncPacket(SyncTraitsPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            MAP.clear();
            packet.getTraits().forEach(trait -> MAP.put(trait.getId(), trait));
            SilentGear.LOGGER.info("Read {} traits from server", MAP.size());
        });
        context.get().setPacketHandled(true);
    }
}

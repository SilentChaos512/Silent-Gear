package net.silentchaos512.gear.traits;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class TraitManager implements IResourceManagerReloadListener {
    public static final TraitManager INSTANCE = new TraitManager();

    public static final Marker MARKER = MarkerManager.getMarker("TraitManager");

    private static final String DATA_PATH = "silentgear_traits";
    private static final String DATA_PATH_OLD = "silentgear/traits";
    private static final Map<ResourceLocation, ITrait> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<ResourceLocation> ERROR_LIST = new ArrayList<>();

    private TraitManager() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = getAllResources(resourceManager);
        if (resources.isEmpty()) return;

        MAP.clear();
        ERROR_LIST.clear();
        SilentGear.LOGGER.info(MARKER, "Reloading trait files");

        for (ResourceLocation id : resources) {
            String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
            ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

            try (IResource iresource = resourceManager.getResource(id)) {
                if (SilentGear.LOGGER.isTraceEnabled()) {
                    SilentGear.LOGGER.trace(MARKER, "Found likely trait file: {}, trying to read as trait {}", id, name);
                }

                JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                if (json == null) {
                    SilentGear.LOGGER.error(MARKER, "could not load trait {} as it's null or empty", name);
                } else {
                    addTrait(TraitSerializers.deserialize(name, json));
                }
            } catch (IllegalArgumentException | JsonParseException ex) {
                SilentGear.LOGGER.error(MARKER, "Parsing error loading trait {}", name, ex);
                ERROR_LIST.add(name);
            } catch (IOException ex) {
                SilentGear.LOGGER.error(MARKER, "Could not read trait {}", name, ex);
                ERROR_LIST.add(name);
            }
        }

        SilentGear.LOGGER.info(MARKER, "Registered {} traits", MAP.size());
    }

    private static Collection<ResourceLocation> getAllResources(IResourceManager resourceManager) {
        Collection<ResourceLocation> list = new ArrayList<>();
        list.addAll(resourceManager.getAllResourceLocations(DATA_PATH, s -> s.endsWith(".json")));
        list.addAll(resourceManager.getAllResourceLocations(DATA_PATH_OLD, s -> s.endsWith(".json")));
        return list;
    }

    private static void addTrait(ITrait trait) {
        if (MAP.containsKey(trait.getId())) {
            throw new IllegalArgumentException("Duplicate trait " + trait.getId());
        } else {
            MAP.put(trait.getId(), trait);
        }
    }

    public static Collection<ResourceLocation> getKeys() {
        synchronized (MAP) {
            return MAP.keySet();
        }
    }

    public static Collection<ITrait> getValues() {
        synchronized (MAP) {
            return MAP.values();
        }
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
        MAP.clear();
        packet.getTraits().forEach(trait -> MAP.put(trait.getId(), trait));
        SilentGear.LOGGER.info("Read {} traits from server", MAP.size());
        context.get().setPacketHandled(true);
    }

    public static Collection<ITextComponent> getErrorMessages(ServerPlayerEntity player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = ERROR_LIST.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            return ImmutableList.of(
                    new StringTextComponent("[Silent Gear] The following traits failed to load, check your log file:")
                            .applyTextStyle(TextFormatting.RED),
                    new StringTextComponent(listStr)
            );
        }
        return ImmutableList.of();
    }
}

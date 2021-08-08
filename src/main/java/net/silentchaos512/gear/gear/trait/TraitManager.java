package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
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
public final class TraitManager implements ResourceManagerReloadListener {
    public static final TraitManager INSTANCE = new TraitManager();

    public static final Marker MARKER = MarkerManager.getMarker("TraitManager");

    private static final String DATA_PATH = "silentgear_traits";
    private static final String DATA_PATH_OLD = "silentgear/traits";
    private static final Map<ResourceLocation, ITrait> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<ResourceLocation> ERROR_LIST = new ArrayList<>();

    private TraitManager() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = getAllResources(resourceManager);
        if (resources.isEmpty()) return;

        MAP.clear();
        ERROR_LIST.clear();
        SilentGear.LOGGER.info(MARKER, "Reloading trait files");

        for (ResourceLocation id : resources) {
            String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
            ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

            try (Resource iresource = resourceManager.getResource(id)) {
                if (SilentGear.LOGGER.isTraceEnabled()) {
                    SilentGear.LOGGER.trace(MARKER, "Found likely trait file: {}, trying to read as trait {}", id, name);
                }

                JsonObject json = GsonHelper.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
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

    private static Collection<ResourceLocation> getAllResources(ResourceManager resourceManager) {
        Collection<ResourceLocation> list = new ArrayList<>();
        list.addAll(resourceManager.listResources(DATA_PATH, s -> s.endsWith(".json")));
        list.addAll(resourceManager.listResources(DATA_PATH_OLD, s -> s.endsWith(".json")));
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
        synchronized (MAP) {
            Map<ResourceLocation, ITrait> oldTraits = ImmutableMap.copyOf(MAP);
            MAP.clear();
            for (ITrait trait : packet.getTraits()) {
                trait.retainData(oldTraits.get(trait.getId()));
                MAP.put(trait.getId(), trait);
            }
            SilentGear.LOGGER.info("Read {} traits from server", MAP.size());
        }
        context.get().setPacketHandled(true);
    }

    public static Collection<Component> getErrorMessages(ServerPlayer player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = ERROR_LIST.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            return ImmutableList.of(
                    new TextComponent("[Silent Gear] The following traits failed to load, check your log file:")
                            .withStyle(ChatFormatting.RED),
                    new TextComponent(listStr)
            );
        }
        return ImmutableList.of();
    }
}

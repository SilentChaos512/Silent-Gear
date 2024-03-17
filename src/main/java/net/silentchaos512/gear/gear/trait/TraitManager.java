package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.TraitJsonException;
import net.silentchaos512.gear.network.SyncTraitsPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

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
    private static final Map<ResourceLocation, ITrait> TRAITS = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<ResourceLocation> ERROR_LIST = new ArrayList<>();

    private TraitManager() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DATA_PATH, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (TRAITS) {
            TRAITS.clear();
            ERROR_LIST.clear();
            SilentGear.LOGGER.info(MARKER, "Reloading trait files");

            String packName = "ERROR";
            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource iresource = resourceOptional.get();
                    packName = iresource.sourcePackId();
                    JsonObject json = null;
                    try {
                        json = GsonHelper.fromJson(gson, IOUtils.toString(iresource.open(), StandardCharsets.UTF_8), JsonObject.class);
                    } catch (IOException ex) {
                        SilentGear.LOGGER.error(MARKER, "Could not read trait {}", name, ex);
                        ERROR_LIST.add(name);
                    }

                    if (json == null) {
                        SilentGear.LOGGER.error(MARKER, "could not load trait {} as it's null or empty", name);
                    } else {
                        addTrait(tryDeserialize(name, packName, json));
                    }
                }
            }
        }

        SilentGear.LOGGER.info(MARKER, "Registered {} traits", TRAITS.size());
    }

    @NotNull
    private static ITrait tryDeserialize(ResourceLocation name, String packName, JsonObject json) {
        SilentGear.LOGGER.info("Deserializing trait {} in pack {}", name, packName);
        try {
            return TraitSerializers.deserialize(name, json);
        } catch (JsonSyntaxException ex) {
            throw new TraitJsonException(name, packName, ex);
        }
    }

    private static void addTrait(ITrait trait) {
        if (TRAITS.containsKey(trait.getId())) {
            throw new IllegalArgumentException("Duplicate trait " + trait.getId());
        } else {
            TRAITS.put(trait.getId(), trait);
        }
    }

    public static Collection<ResourceLocation> getKeys() {
        synchronized (TRAITS) {
            return TRAITS.keySet();
        }
    }

    public static Collection<ITrait> getValues() {
        synchronized (TRAITS) {
            return TRAITS.values();
        }
    }

    @Nullable
    public static ITrait get(ResourceLocation id) {
        return TRAITS.get(id);
    }

    @Nullable
    public static ITrait get(String strId) {
        return get(new ResourceLocation(strId));
    }

    public static void handleTraitSyncPacket(SyncTraitsPacket packet, Supplier<NetworkEvent.Context> context) {
        synchronized (TRAITS) {
            Map<ResourceLocation, ITrait> oldTraits = ImmutableMap.copyOf(TRAITS);
            TRAITS.clear();
            for (ITrait trait : packet.getTraits()) {
                trait.retainData(oldTraits.get(trait.getId()));
                TRAITS.put(trait.getId(), trait);
            }
            SilentGear.LOGGER.info("Read {} traits from server", TRAITS.size());
        }
        context.get().setPacketHandled(true);
    }

    public static Collection<Component> getErrorMessages(ServerPlayer player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = ERROR_LIST.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            return ImmutableList.of(
                    Component.literal("[Silent Gear] The following traits failed to load, check your log file:")
                            .withStyle(ChatFormatting.RED),
                    Component.literal(listStr)
            );
        }
        return ImmutableList.of();
    }
}

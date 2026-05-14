package net.silentchaos512.gear.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silentchaos512.gear.gear.GearJsonException;
import net.silentchaos512.gear.network.payload.server.DataResourcesPayload;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataResourceManager<T> implements ResourceManagerReloadListener, Iterable<T> {
    private final Codec<T> codec;
    private final JsonExceptionFactory<?> exceptionFactory;
    private final String typeName;
    private final String dataPath;
    private final Marker logMarker;
    private final Logger logger;
    private final Map<ResourceLocation, T> byKey = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<T, ResourceLocation> byValue = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<ResourceLocation, String> packNameByKey = new HashMap<>();
    private final List<T> values = new ArrayList<>();
    private final Collection<ResourceLocation> errorList = new ArrayList<>();
    private final Codec<T> byNameCodec;

    private boolean reloading = false;

    public DataResourceManager(Codec<T> codec, JsonExceptionFactory<?> exceptionFactory, String typeName, String dataPath, String logMarkerName, Logger logger) {
        this.codec = codec;
        this.exceptionFactory = exceptionFactory;
        this.typeName = typeName;
        this.dataPath = dataPath;
        this.logMarker = MarkerManager.getMarker(logMarkerName);
        this.logger = logger;
        this.byNameCodec = ResourceLocation.CODEC.flatXmap(
                id -> Optional.ofNullable(get(id))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown " + this.typeName + " key: " + id)),
                component -> Optional.of(getKey(component))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown " + this.typeName + ": " + component))
        );
    }

    public boolean isReloading() {
        return this.reloading;
    }

    public void validate(T value, JsonObject json) {
        // Do nothing
    }

    public void onReloadPre() {
        // Do nothing
    }

    public void onReloadPost() {
        // Do nothing
    }

    public void attachExtraData(T value, String packName, JsonObject json) {
        // Do nothing
    }

    public Codec<T> byNameCodec() {
        return byNameCodec;
    }

    @Nullable
    public T get(ResourceLocation key) {
        return this.byKey.get(key);
    }

    public ResourceLocation getKey(T value) {
        return this.byValue.get(value);
    }

    @Nonnull
    public String getPackName(T value) {
        if (this.byValue.containsKey(value)) {
            return this.packNameByKey.get(getKey(value));
        }
        return "UNKNOWN PACK";
    }

    public Set<ResourceLocation> keySet() {
        return this.byKey.keySet();
    }

    public Set<Map.Entry<ResourceLocation, T>> entrySet() {
        return this.byKey.entrySet();
    }

    public Map<ResourceLocation, T> copyOfMap() {
        return Util.make(() -> {
            ImmutableMap.Builder<ResourceLocation, T> builder = new ImmutableMap.Builder<>();
            entrySet().forEach(entry -> builder.put(entry.getKey(), entry.getValue()));
            return builder.build();
        });
    }

    public Optional<T> getRandom(RandomSource random) {
        return Util.getRandomSafe(this.values, random);
    }

    public boolean containsKey(ResourceLocation name) {
        return this.byKey.containsKey(name);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        if (this.isReloading()) {
            return Collections.emptyIterator();
        }
        synchronized (this.byKey) {
            return this.byKey.values().iterator();
        }
    }

    public Collection<Component> getErrorMessages(ServerPlayer player) {
        if (!this.errorList.isEmpty()) {
            String listStr = this.errorList.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            return ImmutableList.of(
                    Component.literal("[Silent Gear] The following " + this.typeName + "s failed to load, check your log file:")
                            .withStyle(ChatFormatting.RED),
                    Component.literal(listStr)
            );
        }
        return ImmutableList.of();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(this.dataPath, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        onReloadPre();
        this.reloading = true;

        synchronized (this.byKey) {
            this.byKey.clear();
            this.packNameByKey.clear();
            this.errorList.clear();
            this.logger.info(this.logMarker, "Reloading {} files", this.typeName);

            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(this.dataPath.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource resource = resourceOptional.get();
                    String packName = resource.sourcePackId();
                    JsonObject json = null;
                    try {
                        var string = IOUtils.toString(resource.open(), StandardCharsets.UTF_8);
                        json = GsonHelper.fromJson(gson, string, JsonObject.class);
                    } catch (IOException ex) {
                        this.logger.error(this.logMarker, "Could not read {}: {}", this.typeName, name, ex);
                        this.errorList.add(name);
                    }

                    if (json == null) {
                        this.logger.error(this.logMarker, "Could not load {} \"{}\" as it's null or empty", this.typeName, name);
                    } else {
                        var value = tryDecode(name, packName, json);
                        validate(value, json);
                        attachExtraData(value, packName, json);
                        tryAddObject(name, value);
                        this.packNameByKey.put(name, packName);
                    }
                }
            }
        }

        synchronized (this.byValue) {
            // Copy to the byValue map
            this.byValue.clear();
            this.byKey.forEach((key, value) -> this.byValue.put(value, key));
        }

        this.logger.info(this.logMarker, "Decoded {} {}s", this.byKey.size(), this.typeName);
        onReloadPost();
        this.reloading = false;
    }

    private void tryAddObject(ResourceLocation id, T value) {
        if (this.byKey.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate " + this.typeName + ": " + id);
        } else {
            this.byKey.put(id, value);
            this.values.add(value);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private T tryDecode(ResourceLocation name, String packName, JsonObject json) {
        this.logger.info(this.logMarker, "Decoding {} \"{}\" in pack \"{}\"", this.typeName, name, packName);

        DataResult<Pair<T, JsonElement>> result;
        try {
            result = this.codec.decode(JsonOps.INSTANCE, json);
        } catch (Exception ex) {
            this.logger.info(this.logMarker, "Error decoding {} \"{}\" in pack \"{}\"", this.typeName, name, packName);
            throw this.exceptionFactory.create(name, packName, ex);
        }

        if (result.isSuccess()) {
            return result.result().get().getFirst();
        } else {
            var cause = new JsonSyntaxException(result.error().get().message());
            throw this.exceptionFactory.create(name, packName, cause);
        }
    }

    @FunctionalInterface
    public interface JsonExceptionFactory<E extends GearJsonException> {
        E create(ResourceLocation resourceName, String packName, Throwable cause);
    }

    public void handleSyncPacket(DataResourcesPayload<T> data, IPayloadContext ctx) {
        synchronized (this.byKey) {
            this.byKey.clear();
            this.byKey.putAll(data.values());
        }
        synchronized (this.byValue) {
            this.byValue.clear();
            this.values.clear();
            this.byKey.forEach((key, value) -> {
                this.byValue.put(value, key);
                this.values.add(value);
            });
        }
        this.logger.info(this.logMarker, "Received {} {}s from server", this.byKey.size(), this.typeName);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}

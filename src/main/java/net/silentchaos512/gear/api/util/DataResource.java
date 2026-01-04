package net.silentchaos512.gear.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DataResource<T> implements Supplier<T> {
    private final Identifier objectId;
    private final Function<Identifier, T> getter;

    public DataResource(Identifier id, Function<Identifier, T> getter) {
        this.objectId = id;
        this.getter = getter;
    }

    public static <T> DataResource<T> empty() {
        return new DataResource<>(SilentGear.getId("empty"), id -> null);
    }

    public static DataResource<Material> material(String modPath) {
        return material(SilentGear.getId(modPath));
    }

    public static DataResource<Material> material(Identifier id) {
        return new DataResource<>(id, SgRegistries.MATERIAL::get);
    }

    public static DataResource<Material> material(Material material) {
        return material(SgRegistries.MATERIAL.getKey(material));
    }

    public static DataResource<Material> material(MaterialInstance materialInstance) {
        return material(materialInstance.get());
    }

    public static DataResource<GearPart> part(String modPath) {
        return part(SilentGear.getId(modPath));
    }

    public static DataResource<GearPart> part(Identifier id) {
        return new DataResource<>(id, SgRegistries.PART::get);
    }

    public static DataResource<Trait> trait(String modPath) {
        return trait(SilentGear.getId(modPath));
    }

    public static DataResource<Trait> trait(Identifier id) {
        return new DataResource<>(id, SgRegistries.TRAIT::get);
    }

    @Nullable
    public T getNullable() {
        return this.getter.apply(this.objectId);
    }

    public Optional<DataResource<T>> toOptional() {
        if (!isPresent()) {
            return Optional.empty();
        }
        return Optional.of(this);
    }

    @Override
    public T get() {
        T ret = getNullable();
        Objects.requireNonNull(ret, () -> "Data resource not present: " + this.objectId);
        return ret;
    }

    public Identifier getId() {
        return this.objectId;
    }

    public boolean isPresent() {
        return this.getNullable() != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T obj = getNullable();
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    public Stream<T> stream() {
        return isPresent() ? Stream.of(get()) : Stream.of();
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        T obj = getNullable();
        return obj != null ? Optional.ofNullable(mapper.apply(obj)) : Optional.empty();
    }

    @Override
    public String toString() {
        return "DataResource{" + this.objectId + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof DataResource<?> other)) return false;

        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    public static final Codec<DataResource<Material>> MATERIAL_CODEC = Identifier.CODEC.xmap(
            DataResource::material,
            DataResource::getId
    );
    public static final Codec<DataResource<GearPart>> PART_CODEC = Identifier.CODEC.xmap(
            DataResource::part,
            DataResource::getId
    );
    public static final Codec<DataResource<Trait>> TRAIT_CODEC = Identifier.CODEC.xmap(
            DataResource::trait,
            DataResource::getId
    );

    public static final StreamCodec<FriendlyByteBuf, DataResource<Material>> MATERIAL_STREAM_CODEC = StreamCodec.of(
            (buf, d) -> buf.writeIdentifier(d.getId()),
            buf -> DataResource.material(buf.readIdentifier())
    );
    public static final StreamCodec<FriendlyByteBuf, DataResource<GearPart>> PART_STREAM_CODEC = StreamCodec.of(
            (buf, d) -> buf.writeIdentifier(d.getId()),
            buf -> DataResource.part(buf.readIdentifier())
    );
    public static final StreamCodec<FriendlyByteBuf, DataResource<Trait>> TRAIT_STREAM_CODEC = StreamCodec.of(
            (buf, d) -> buf.writeIdentifier(d.getId()),
            buf -> DataResource.trait(buf.readIdentifier())
    );
}

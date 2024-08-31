package net.silentchaos512.gear.api.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class PropertyKey<T, V extends GearPropertyValue<T>> {
    private static final Map<Pair<GearProperty<?, ? extends GearPropertyValue<?>>, GearType>, PropertyKey<?, ?>> CACHE = new ConcurrentHashMap<>();

    public static final Codec<PropertyKey<?, ? extends GearPropertyValue<?>>> CODEC = Codec.STRING.comapFlatMap(
            PropertyKey::fromString,
            PropertyKey::key
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, PropertyKey<?, ?>> STREAM_CODEC = StreamCodec.of(
            PropertyKey::encode,
            PropertyKey::decode
    );

    private final String key;
    private final GearProperty<T, V> property;
    private final GearType gearType;

    private PropertyKey(GearProperty<T, V> property, GearType gearType) {
        this.property = property;
        this.gearType = gearType;
        this.key = SilentGear.shortenId(SgRegistries.GEAR_PROPERTY.getKey(this.property))
                + makeKeySuffix();
    }

    public static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> PropertyKey<T, V> of(Supplier<P> property, Supplier<GearType> gearType) {
        return of(property.get(), gearType.get());
    }

    public static <T, V extends GearPropertyValue<T>> PropertyKey<T, V> of(GearProperty<T, V> property, GearType gearType) {
        //noinspection unchecked
        return (PropertyKey<T, V>) CACHE.computeIfAbsent(Pair.of(property, gearType), pair ->
                new PropertyKey<>(pair.getFirst(), pair.getSecond()));
    }

    public String key() {
        return key;
    }

    public GearProperty<T, V> property() {
        return property;
    }

    public GearType gearType() {
        return gearType;
    }

    @Nullable
    public PropertyKey<?, ?> getParent() {
        var parent = this.gearType.parent();
        if (parent != null) {
            return of(this.property, parent.get());
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PropertyKey<?, ?>) obj;
        return Objects.equals(this.property, that.property) &&
                Objects.equals(this.gearType, that.gearType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, gearType);
    }

    @Override
    public String toString() {
        return "PropertyKey[" +
                "property=" + property + ", " +
                "gearType=" + gearType + ']';
    }

    private static DataResult<PropertyKey<?, ?>> fromString(String s) {
        var split = s.split("/");
        if (split.length > 2) {
            return DataResult.error(() -> "Invalid key: " + s);
        }

        var property = SgRegistries.GEAR_PROPERTY.get(SilentGear.getIdWithDefaultNamespace(split[0]));
        if (property == null) {
            return DataResult.error(() -> "Unknown gear property: \"" + split[0] + "\" in key " + s);
        }

        GearType gearType;
        if (split.length > 1) {
            gearType = SgRegistries.GEAR_TYPE.get(SilentGear.getIdWithDefaultNamespace(split[1]));
            if (gearType == null || gearType == GearTypes.NONE.get()) {
                return DataResult.error(() -> "Unknown gear type: \"" + split[1] + "\" in key " + s);
            }
        } else {
            gearType = GearTypes.ALL.get();
        }

        return DataResult.success(new PropertyKey<>(property, gearType));
    }

    private static void encode(RegistryFriendlyByteBuf buf, PropertyKey<?, ?> key) {
        ByteBufCodecs.registry(SgRegistries.GEAR_PROPERTY_KEY).encode(buf, key.property);
        ByteBufCodecs.registry(SgRegistries.GEAR_TYPE_KEY).encode(buf, key.gearType);
    }

    private static PropertyKey<?, ?> decode(RegistryFriendlyByteBuf buf) {
        var property = ByteBufCodecs.registry(SgRegistries.GEAR_PROPERTY_KEY).decode(buf);
        var gearType = ByteBufCodecs.registry(SgRegistries.GEAR_TYPE_KEY).decode(buf);
        return new PropertyKey<>(property, gearType);
    }

    private String makeKeySuffix() {
        if (this.gearType != GearTypes.ALL.get()) {
            return "/" + SilentGear.shortenId(SgRegistries.GEAR_TYPE.getKey(this.gearType));
        }
        return "";
    }
}

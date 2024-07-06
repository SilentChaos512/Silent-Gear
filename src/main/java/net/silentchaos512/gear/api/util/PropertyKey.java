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
import net.silentchaos512.gear.api.property.GearPropertyType;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class PropertyKey {
    private static final Map<Pair<GearPropertyType<?, ? extends GearProperty<?>>, GearType>, PropertyKey> CACHE = new ConcurrentHashMap<>();

    public static final Codec<PropertyKey> CODEC = Codec.STRING.comapFlatMap(
            PropertyKey::fromString,
            PropertyKey::key
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, PropertyKey> STREAM_CODEC = StreamCodec.of(
            PropertyKey::encode,
            PropertyKey::decode
    );

    private final String key;
    private final GearPropertyType<?, ? extends GearProperty<?>> propertyType;
    private final GearType gearType;

    private PropertyKey(GearPropertyType<?, ? extends GearProperty<?>> propertyType, GearType gearType) {
        this.propertyType = propertyType;
        this.gearType = gearType;
        this.key = SilentGear.shortenId(SgRegistries.GEAR_PROPERTIES.getKey(this.propertyType))
                + "/"
                + SilentGear.shortenId(SgRegistries.GEAR_TYPES.getKey(this.gearType));
    }

    public static PropertyKey of(GearPropertyType<?, ? extends GearProperty<?>> property, GearType gearType) {
        return CACHE.computeIfAbsent(Pair.of(property, gearType), pair ->
                new PropertyKey(pair.getFirst(), pair.getSecond()));
    }

    public String key() {
        return key;
    }

    public GearPropertyType<?, ? extends GearProperty<?>> propertyType() {
        return propertyType;
    }

    public GearType gearType() {
        return gearType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PropertyKey) obj;
        return Objects.equals(this.propertyType, that.propertyType) &&
                Objects.equals(this.gearType, that.gearType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyType, gearType);
    }

    @Override
    public String toString() {
        return "PropertyKey[" +
                "property=" + propertyType + ", " +
                "gearType=" + gearType + ']';
    }

    private static DataResult<PropertyKey> fromString(String s) {
        var split = s.split("/");
        if (split.length > 2) {
            return DataResult.error(() -> "Invalid key: " + s);
        }

        var property = SgRegistries.GEAR_PROPERTIES.get(SilentGear.getIdWithDefaultNamespace(split[0]));
        if (property == null) {
            return DataResult.error(() -> "Unknown gear property: \"" + split[0] + "\" in key " + s);
        }

        GearType gearType;
        if (split.length > 1) {
            gearType = SgRegistries.GEAR_TYPES.get(SilentGear.getIdWithDefaultNamespace(split[1]));
            if (gearType == null || gearType == GearTypes.NONE.get()) {
                return DataResult.error(() -> "Unknown gear type: \"" + split[1] + "\" in key " + s);
            }
        } else {
            gearType = GearTypes.ALL.get();
        }

        return DataResult.success(new PropertyKey(property, gearType));
    }

    private static void encode(RegistryFriendlyByteBuf buf, PropertyKey key) {
        ByteBufCodecs.registry(SgRegistries.GEAR_PROPERTIES_KEY).encode(buf, key.propertyType);
        ByteBufCodecs.registry(SgRegistries.GEAR_TYPES_KEY).encode(buf, key.gearType);
    }

    private static PropertyKey decode(RegistryFriendlyByteBuf buf) {
        var property = ByteBufCodecs.registry(SgRegistries.GEAR_PROPERTIES_KEY).decode(buf);
        var gearType = ByteBufCodecs.registry(SgRegistries.GEAR_TYPES_KEY).decode(buf);
        return new PropertyKey(property, gearType);
    }
}

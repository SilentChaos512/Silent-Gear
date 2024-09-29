package net.silentchaos512.gear.core.component;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.CodecUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public record GearPropertiesData(
        Map<GearProperty<?, ? extends GearPropertyValue<?>>, GearPropertyValue<?>> properties
) {
    public static final GearPropertiesData EMPTY = new GearPropertiesData(Collections.emptyMap());

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final Codec<GearPropertiesData> CODEC = Codec
            .dispatchedMap(
                    CodecUtils.byModNameCodec(SgRegistries.GEAR_PROPERTY),
                    GearProperty::codec
            )
            .xmap(
                    map -> {
                        Map<GearProperty<?, ?>, GearPropertyValue<?>> input = new LinkedHashMap<>(map);
                        return new GearPropertiesData(input);
                    },
                    data -> {
                        // Black magic to dance around the generics, don't ask me why it works
                        return (Map) data.properties;
                    }
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, GearPropertiesData> STREAM_CODEC = StreamCodec.of(
            GearPropertiesData::encode,
            GearPropertiesData::decode
    );

    public GearPropertiesData(Map<GearProperty<?, ?>, GearPropertyValue<?>> properties) {
        this.properties = ImmutableMap.copyOf(properties);
    }

    @Nullable
    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> V get(Supplier<P> propertyType) {
        return get(propertyType.get());
    }

    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> V getOrDefault(Supplier<P> propertyType, V defaultValue) {
        return getOrDefault(propertyType.get(), defaultValue);
    }

    @Nullable
    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> V get(P propertyType) {
        //noinspection unchecked
        return (V) properties.get(propertyType);
    }

    public <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> V getOrDefault(P propertyType, V defaultValue) {
        //noinspection unchecked
        return (V) properties.getOrDefault(propertyType, defaultValue);
    }

    public float getNumber(Supplier<NumberProperty> propertyType) {
        return getNumber(propertyType, propertyType.get().getDefaultValue());
    }

    public float getNumber(Supplier<NumberProperty> propertyType, float defaultValue) {
        var property = get(propertyType.get());
        return property != null ? property.value() : defaultValue;
    }

    public float getNumber(NumberProperty propertyType) {
        return getNumber(propertyType, propertyType.getDefaultValue());
    }

    public float getNumber(NumberProperty propertyType, float defaultValue) {
        var property = get(propertyType);
        return property != null ? property.value() : defaultValue;
    }

    public int getNumberInt(Supplier<NumberProperty> propertyType) {
        return Math.round(getNumber(propertyType, propertyType.get().getDefaultValue()));
    }

    public boolean contains(GearProperty<?, ?> property) {
        return this.properties.containsKey(property);
    }

    private static void encode(RegistryFriendlyByteBuf buf, GearPropertiesData data) {
        buf.writeVarInt(data.properties.size());
        data.properties.forEach((property, value) -> {
            var key = SgRegistries.GEAR_PROPERTY.getKey(property);
            assert key != null;
            buf.writeResourceLocation(key);
            property.rawStreamCodec().encode(buf, value);
        });
    }

    private static GearPropertiesData decode(RegistryFriendlyByteBuf buf) {
        Map<GearProperty<?, ?>, GearPropertyValue<?>> map = new LinkedHashMap<>();
        int count = buf.readVarInt();
        for (int i = 0; i < count; ++i) {
            var property = SgRegistries.GEAR_PROPERTY.get(buf.readResourceLocation());
            assert property != null;
            var value = property.rawStreamCodec().decode(buf);
            map.put(property, value);
        }
        return new GearPropertiesData(map);
    }
}

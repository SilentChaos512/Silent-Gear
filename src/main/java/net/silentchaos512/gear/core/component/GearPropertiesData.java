package net.silentchaos512.gear.core.component;

import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyType;
import net.silentchaos512.gear.api.property.NumberPropertyType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public record GearPropertiesData(
        Map<GearPropertyType<?, ?>, GearProperty<?>> properties
) {
    @Nullable
    public <V, P extends GearProperty<V>> P get(Supplier<GearPropertyType<V, P>> propertyType) {
        return get(propertyType.get());
    }

    public <V, P extends GearProperty<V>> P getOrDefault(Supplier<GearPropertyType<V, P>> propertyType, P defaultValue) {
        return getOrDefault(propertyType.get(), defaultValue);
    }

    @Nullable
    public <V, P extends GearProperty<V>> P get(GearPropertyType<V, P> propertyType) {
        //noinspection unchecked
        return (P) properties.get(propertyType);
    }

    public <V, P extends GearProperty<V>> P getOrDefault(GearPropertyType<V, P> propertyType, P defaultValue) {
        //noinspection unchecked
        return (P) properties.getOrDefault(propertyType, defaultValue);
    }

    public float getNumber(Supplier<NumberPropertyType> propertyType) {
        return getNumber(propertyType, propertyType.get().getDefaultValue());
    }

    public float getNumber(Supplier<NumberPropertyType> propertyType, float defaultValue) {
        var property = get(propertyType.get());
        return property != null ? property.value() : defaultValue;
    }

    public float getNumber(NumberPropertyType propertyType) {
        return getNumber(propertyType, propertyType.getDefaultValue());
    }

    public float getNumber(NumberPropertyType propertyType, float defaultValue) {
        var property = get(propertyType);
        return property != null ? property.value() : defaultValue;
    }
}

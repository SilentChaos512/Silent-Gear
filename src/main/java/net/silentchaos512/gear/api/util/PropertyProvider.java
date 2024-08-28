package net.silentchaos512.gear.api.util;

import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Something that can provide property modifiers, such as parts and materials
 *
 * @param <D> An object containing more data about this object, such as
 * {@link net.silentchaos512.gear.gear.part.PartInstance} or
 * {@link net.silentchaos512.gear.gear.material.MaterialInstance}
 */
public interface PropertyProvider<D> {
    <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(D instance, PartType partType, PropertyKey<T, V> key);

    default <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(D instance, Supplier<PartType> partType, PropertyKey<T, V> key) {
        return getPropertyModifiers(instance, partType.get(), key);
    }

    default <T, V extends GearPropertyValue<T>> T getProperty(D instance, PartType partType, PropertyKey<T, V> key) {
        var property = key.property();
        var mods = getPropertyModifiers(instance, partType, key);
        return property.compute(mods);
    }

    default <T, V extends GearPropertyValue<T>> T getProperty(D instance, Supplier<PartType> partType, PropertyKey<T, V> key) {
        return getProperty(instance, partType.get(), key);
    }

    default <T, V extends GearPropertyValue<T>> T getPropertyUnclamped(D instance, PartType partType, PropertyKey<T, V> key) {
        var property = key.property();
        var mods = getPropertyModifiers(instance, partType, key);
        return property.compute(property.getBaseValue(), false, key.gearType(), mods);
    }

    default <T, V extends GearPropertyValue<T>> T getPropertyUnclamped(D instance, Supplier<PartType> partType, PropertyKey<T, V> key) {
        return getPropertyUnclamped(instance, partType.get(), key);
    }
}

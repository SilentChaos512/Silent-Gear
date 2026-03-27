package net.silentchaos512.gear.api.util;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.ComputeContext;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public interface GearComponentInstance<A extends GearComponent<?>> {
    @Nullable
    A get();

    Identifier getId();

    @Nullable ItemStackTemplate getItem();

    default ItemStack copyItem() {
        var item = getItem();
        return item != null ? item.create() : ItemStack.EMPTY;
    }

    default <T> @Nullable T getItemData(Supplier<? extends DataComponentType<? extends T>> componentType) {
        return getItemData(componentType.get());
    }

    default <T> @Nullable T getItemData(DataComponentType<? extends T> componentType) {
        var item = getItem();
        if (item == null) return null;
        return item.get(componentType);
    }

    default <T> T getItemData(Supplier<? extends DataComponentType<? extends T>> componentType, T defaultValue) {
        return getItemData(componentType.get(), defaultValue);
    }

    default <T> T getItemData(DataComponentType<? extends T> componentType, T defaultValue) {
        var item = getItem();
        if (item == null) return defaultValue;
        return item.getOrDefault(componentType, defaultValue);
    }

    boolean is(DataResource<A> resource);

    <T, V extends GearPropertyValue<T>> T getProperty(PartType partType, PropertyKey<T, V> key);

    default <T, V extends GearPropertyValue<T>> T getProperty(PartType partType, GearProperty<T, V> property) {
        return getProperty(partType, PropertyKey.of(property, GearTypes.ALL.get()));
    }

    default <T, V extends GearPropertyValue<T>> T getProperty(Supplier<PartType> partType, PropertyKey<T, V> key) {
        return getProperty(partType.get(), key);
    }

    <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PartType partType, PropertyKey<T, V> key);

    default <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(Supplier<PartType> partType, PropertyKey<T, V> key) {
        return getPropertyModifiers(partType.get(), key);
    }

    default Collection<TraitInstance> getTraits(PartGearKey key) {
        var property = GearProperties.TRAITS.get();
        var mods = getPropertyModifiers(key.partType(), PropertyKey.of(property, key.gearType()));
        return property.compute(ComputeContext.from(this, key.partType()), mods);
    }

    default Component getDisplayName(PartType type) {
        return getDisplayName(type, ItemStack.EMPTY);
    }

    Component getDisplayName(PartType type, ItemStack gear);

    int getNameColor(PartType partType, GearType gearType);

    default int getNameColor() {
        return getNameColor(PartTypes.MAIN.get(), GearTypes.ALL.get());
    }
}

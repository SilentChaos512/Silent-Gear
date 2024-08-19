package net.silentchaos512.gear.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.setup.gear.GearTypes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public interface GearComponentInstance<A extends GearComponent<?>> {
    @Nullable
    A get();

    ResourceLocation getId();

    ItemStack getItem();

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

    Collection<TraitInstance> getTraits(PartGearKey key);

    default Component getDisplayName(PartType type) {
        return getDisplayName(type, ItemStack.EMPTY);
    }

    Component getDisplayName(PartType type, ItemStack gear);

    int getNameColor(PartType partType, GearType gearType);
}

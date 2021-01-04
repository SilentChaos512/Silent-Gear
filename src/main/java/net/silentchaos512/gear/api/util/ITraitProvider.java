package net.silentchaos512.gear.api.util;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;

import java.util.Collection;

public interface ITraitProvider<D> {
    Collection<TraitInstance> getTraits(D instance, PartType partType, GearType gearType, ItemStack gear);
}

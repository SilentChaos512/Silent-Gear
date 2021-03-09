package net.silentchaos512.gear.api.util;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.traits.TraitInstance;

import java.util.Collection;

public interface ITraitProvider<D> {
    Collection<TraitInstance> getTraits(D instance, PartGearKey partKey, ItemStack gear);
}

package net.silentchaos512.gear.api.util;

import net.silentchaos512.gear.api.traits.TraitInstance;

import java.util.Collection;

public interface TraitProvider<D> {
    @Deprecated
    Collection<TraitInstance> getTraits(D instance, PartGearKey partKey);
}

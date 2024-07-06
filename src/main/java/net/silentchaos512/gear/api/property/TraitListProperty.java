package net.silentchaos512.gear.api.property;

import net.silentchaos512.gear.api.traits.TraitInstance;

import java.util.List;

public class TraitListProperty extends GearProperty<List<TraitInstance>> {
    public TraitListProperty(List<TraitInstance> value) {
        super(value);
    }
}

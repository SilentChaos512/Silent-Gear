package net.silentchaos512.gear.api.property;

import net.minecraft.world.item.Tier;

public class TierPropertyValue extends GearPropertyValue<Tier> {
    public TierPropertyValue(Tier value) {
        super(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}

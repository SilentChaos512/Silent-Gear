package net.silentchaos512.gear.api.property;

import net.minecraft.world.item.Tier;
import net.silentchaos512.gear.setup.gear.GearPropertyTypes;

public class TierPropertyValue extends GearPropertyValue<Tier> {
    public TierPropertyValue(Tier value) {
        super(value);
    }

    @Override
    public GearPropertyType<?> type() {
        return GearPropertyTypes.TIER.get();
    }
}

package net.silentchaos512.gear.api.property;

public class HarvestTierPropertyValue extends GearPropertyValue<HarvestTier> {
    public HarvestTierPropertyValue(HarvestTier value) {
        super(value);
    }

    @Override
    public String toString() {
        return this.value.name();
    }
}

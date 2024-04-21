package net.silentchaos512.gear.api.stats;

public record ChargedProperties(int chargeLevel, float chargeValue) {
    public float getChargeValue() {
        return chargeLevel * chargeValue;
    }
}

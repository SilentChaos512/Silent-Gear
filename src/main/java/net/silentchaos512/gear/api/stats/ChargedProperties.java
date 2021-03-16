package net.silentchaos512.gear.api.stats;

public class ChargedProperties {
    private final int chargeLevel;
    private final float chargeability;

    public ChargedProperties(int chargeLevel, float chargeability) {
        this.chargeLevel = chargeLevel;
        this.chargeability = chargeability;
    }

    public float getChargeValue() {
        return chargeLevel * chargeability;
    }

    public int getChargeLevel() {
        return chargeLevel;
    }

    public float getChargeability() {
        return chargeability;
    }
}

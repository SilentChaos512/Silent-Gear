package net.silentchaos512.gear.client.tooltip;

public record GearTooltipStyle(
        boolean showHiddenValues,
        boolean colorPropertyName,
        boolean compactStylePreferred
) {
    public GearTooltipStyle withoutCompactStyle() {
        return new GearTooltipStyle(this.showHiddenValues, this.colorPropertyName, false);
    }
}

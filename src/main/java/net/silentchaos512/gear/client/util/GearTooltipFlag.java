package net.silentchaos512.gear.client.util;

import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.client.KeyTracker;

public record GearTooltipFlag(
        boolean ctrlDown,
        boolean altDown,
        boolean shiftDown,
        boolean advanced,
        boolean showProperties,
        boolean showConstruction
) implements TooltipFlag {
    public static GearTooltipFlag withModifierKeys(boolean advanced, boolean showProperties, boolean showConstruction) {
        return new GearTooltipFlag(KeyTracker.isControlDown(), KeyTracker.isAltDown(), KeyTracker.isShiftDown(), advanced, showProperties, showConstruction);
    }

    @Override
    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}

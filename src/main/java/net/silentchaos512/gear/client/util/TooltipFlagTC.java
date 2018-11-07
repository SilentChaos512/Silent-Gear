package net.silentchaos512.gear.client.util;

import net.minecraft.client.util.ITooltipFlag;
import net.silentchaos512.gear.client.KeyTracker;

// TODO: Add flag to indicate crafting station is being used (allow extra info only in cs)
// FIXME: Class name (replace TC)
public class TooltipFlagTC implements ITooltipFlag {
    public final boolean ctrlDown;
    public final boolean altDown;
    public final boolean shiftDown;
    public final boolean advanced;
    public final boolean showStats;
    public final boolean showConstruction;

    public TooltipFlagTC(boolean ctrlDown, boolean altDown, boolean shiftDown, boolean advanced) {
        this(ctrlDown, altDown, shiftDown, advanced, true, true);
    }

    public TooltipFlagTC(boolean ctrlDown, boolean altDown, boolean shiftDown, boolean advanced, boolean showStats, boolean showConstruction) {
        this.ctrlDown = ctrlDown;
        this.altDown = altDown;
        this.shiftDown = shiftDown;
        this.advanced = advanced;
        this.showStats = showStats;
        this.showConstruction = showConstruction;
    }

    public static TooltipFlagTC withModifierKeys(boolean advanced, boolean showStats, boolean showConstruction) {
        return new TooltipFlagTC(KeyTracker.isControlDown(), KeyTracker.isAltDown(), KeyTracker.isShiftDown(), advanced, showStats, showConstruction);
    }

    @Override
    public boolean isAdvanced() {
        return advanced;
    }
}

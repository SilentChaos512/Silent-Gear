package net.silentchaos512.gear.client.tooltip;

import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;

public record FormatColorScheme(
        @Nullable Color plain,
        @Nullable Color zero,
        @Nullable Color positive,
        @Nullable Color negative
) {
    public static final FormatColorScheme NO_COLORS = new FormatColorScheme(null, null, null, null);
    public static final FormatColorScheme WHITE_ONLY = new FormatColorScheme(Color.WHITE, null, null, null);
    public static final FormatColorScheme LIGHT = new FormatColorScheme(Color.WHITE, null, Color.LIGHTGREEN, Color.INDIANRED);
    public static final FormatColorScheme DARK = new FormatColorScheme(Color.BLACK, null, Color.DARKGREEN, Color.DARKRED);
    public static final FormatColorScheme DARK_GREY_ZERO_OR_WHITE = new FormatColorScheme(Color.WHITE, TooltipHandler.MC_DARK_GRAY, null, null);
    public static final FormatColorScheme DARK_GREY_ZERO_OR_NO_COLOR = new FormatColorScheme(null, TooltipHandler.MC_DARK_GRAY, null, null);

    @Nullable
    public Color getColor(float value, float comparison) {
        if (this.plain != null && MathUtils.floatsEqual(value, comparison)) {
            return this.plain;
        }
        if (this.zero != null && MathUtils.floatsEqual(value, 0f)) {
            return this.zero;
        }
        if (this.positive != null && value > comparison + 0.001f) {
            return this.positive;
        }
        if (this.negative != null && value < comparison - 0.001f) {
            return this.negative;
        }
        return this.plain;
    }
}

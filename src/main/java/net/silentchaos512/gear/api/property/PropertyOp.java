package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nonnegative;
import java.util.regex.Pattern;

public enum PropertyOp {
    AVERAGE("avg"),
    MAX("max"),
    ADD("add"),
    MULTIPLY_BASE("mul1"),
    MULTIPLY_TOTAL("mul2");

    public static final Codec<PropertyOp> CODEC = Codec.STRING.comapFlatMap(
            s -> {
                for (PropertyOp op : values()) {
                    if (s.equalsIgnoreCase(op.name()) || s.equalsIgnoreCase(op.alias)) {
                        return DataResult.success(op);
                    }
                }
                return DataResult.error(() -> "Unknown operation: " + s);
            },
            Enum::name
    );

    private final String alias;

    PropertyOp(String alias) {
        this.alias = alias;
    }

    public MutableComponent formatNumberValue(NumberPropertyType property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
        return switch (this) {
            case ADD ->
                // +/-v
                    formatAdd(property, value, decimalPlaces, addColor);
            case AVERAGE ->
                // v or vx
                    formatAvg(property, value, decimalPlaces, addColor);
            case MAX ->
                // ^v
                    formatMax(property, value, decimalPlaces, addColor);
            case MULTIPLY_BASE ->
                // +/-v%
                    formatMul1(property, value, decimalPlaces, addColor);
            case MULTIPLY_TOTAL ->
                // vx
                    formatMul2(property, value, decimalPlaces, addColor);
        };
    }

    //region Private formatted text methods

    private static final Pattern REGEX_TRIM_TO_INT = Pattern.compile("\\.0+$");
    private static final Pattern REGEX_REMOVE_TRAILING_ZEROS = Pattern.compile("0+$");

    private MutableComponent formatAdd(NumberPropertyType property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        Color color = getFormattedColor(value, 0f, addColor);
        String text = trimNumber(String.format(format, value < 0 ? "" : "+", value));
        return TextUtil.withColor(Component.literal(text), color);
    }

    private MutableComponent formatAvg(NumberPropertyType property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
        Color color = getFormattedColor(value, 0f, addColor);
        String text;
        if (property.getDisplayFormat() == NumberPropertyType.DisplayFormat.PERCENTAGE) {
            text = Math.round(value * 100) + "%";
        } else {
            // v (or vx for multiplier stats like armor durability)
            String format = "%s" + ("%." + decimalPlaces + "f") + "%s";
            String ret = trimNumber(String.format(format, "", value, ""));
            text = property.getDisplayFormat() == NumberPropertyType.DisplayFormat.MULTIPLIER ? ret + "x" : ret;
        }
        return TextUtil.withColor(Component.literal(text), color);
    }

    private MutableComponent formatMax(NumberPropertyType property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        String text = trimNumber(String.format(format, "↑", value)); //u2191
        return TextUtil.withColor(Component.literal(text), Color.WHITE);
    }

    private MutableComponent formatMul1(NumberPropertyType property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
        int percent = Math.round(100 * value);
        Color color = getFormattedColor(percent, 0f, addColor);
        String text = trimNumber(String.format("%s%d%%", percent < 0 ? "" : "+", percent));
        return TextUtil.withColor(Component.literal(text), color);
    }

    private MutableComponent formatMul2(NumberPropertyType property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        float val = 1f + value;
        Color color = getFormattedColor(val, 1f, addColor);
        String text = trimNumber(String.format(format, "x", val));
        return TextUtil.withColor(Component.literal(text), color);
    }

    private static String trimNumber(CharSequence str) {
        // Trim number to an int if possible, or just trim off any trailing zeros
        String trimToInt = REGEX_TRIM_TO_INT.matcher(str).replaceFirst("");
        if (trimToInt.contains("."))
            return REGEX_REMOVE_TRAILING_ZEROS.matcher(trimToInt).replaceFirst("");
        return trimToInt;
    }

    private static Color getFormattedColor(float val, float whiteVal, boolean addColor) {
        if (!addColor) return Color.WHITE;
        return val < whiteVal ? Color.INDIANRED : MathUtils.floatsEqual(val, whiteVal) ? Color.WHITE : Color.LIGHTGREEN;
    }

    //endregion
}

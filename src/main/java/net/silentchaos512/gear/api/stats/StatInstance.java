package net.silentchaos512.gear.api.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;
import net.silentchaos512.utils.MathUtils;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents either a stat modifier or a calculated stat value.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public class StatInstance {
    public enum Operation {
        AVG, MAX, MUL1, MUL2, ADD;

        public static Operation byName(String str) {
            for (Operation op : values())
                if (op.name().equalsIgnoreCase(str))
                    return op;
            return AVG;
        }

        @Nullable
        public static Operation byNameOrNull(String str) {
            for (Operation op : values())
                if (op.name().equalsIgnoreCase(str))
                    return op;
            return null;
        }
    }

    private static final Pattern REGEX_TRIM_TO_INT = Pattern.compile("\\.0+$");
    private static final Pattern REGEX_REMOVE_TRAILING_ZEROS = Pattern.compile("0+$");

    final float value;
    final Operation op;

    // deprecated: Use "of" static factory method
    @Deprecated
    public StatInstance(float value, Operation op) {
        this.value = value;
        this.op = op;
    }

    public static StatInstance of(float value) {
        return of(value, Operation.AVG);
    }

    public static StatInstance of(float value, Operation op) {
        return new StatInstance(value, op);
    }

    public static StatInstance of(float value, String source) {
        return of(value, Operation.AVG, source);
    }

    public static StatInstance of(float value, Operation op, String source) {
        return new StatInstanceWithSource(value, op, source);
    }

    public StatInstance copySetValue(float newValue) {
        return of(newValue, this.op);
    }

    // TODO: Is this method needed?
    public StatInstance copy() {
        return new StatInstance(this.value, this.op);
    }

    /**
     * Get the value of the stat or stat modifier
     *
     * @return The modifier value
     */
    public float getValue() {
        return value;
    }

    /**
     * Get the operator of the stat modifier
     *
     * @return The modifier operator
     */
    public Operation getOp() {
        return op;
    }

    public String getSource() {
        return "N/A";
    }

    @Deprecated
    public static StatInstance makeBaseMod(float value) {
        return new StatInstance(value, Operation.ADD);
    }

    @Deprecated
    public static StatInstance makeGearMod(float multi) {
        return new StatInstance(multi, Operation.MUL1);
    }

    public static StatInstance getWeightedAverageMod(Collection<StatInstance> modifiers, Operation op) {
        return new StatInstance(ItemStat.getWeightedAverage(modifiers, op), op);
    }

    public IFormattableTextComponent getFormattedText(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        switch (this.op) {
            case ADD:
                // +/-v
                return formatAdd(stat, decimalPlaces, addColor);
            case AVG:
                // v or vx
                return formatAvg(stat, decimalPlaces, addColor);
            case MAX:
                // ^v
                return formatMax(stat, decimalPlaces, addColor);
            case MUL1:
                // +/-v%
                return formatMul1(stat, decimalPlaces, addColor);
            case MUL2:
                // vx
                return formatMul2(stat, decimalPlaces, addColor);
            default:
                throw new NotImplementedException("Unknown operation: " + op);
        }
    }

    //region Private formatted text methods

    private IFormattableTextComponent formatAdd(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        Color color = getFormattedColor(this.value, 0f, addColor);
        String text = trimNumber(String.format(format, this.value < 0 ? "" : "+", this.value));
        return TextUtil.withColor(new StringTextComponent(text), color);
    }

    private IFormattableTextComponent formatAvg(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        Color color = getFormattedColor(this.value, 0f, addColor);
        String text;
        if (stat.getDisplayFormat() == ItemStat.DisplayFormat.PERCENTAGE) {
            text = Math.round((1f + this.value) * 100) + "%";
        } else {
            // v (or vx for multiplier stats like armor durability)
            String format = "%s" + ("%." + decimalPlaces + "f") + "%s";
            String ret = trimNumber(String.format(format, "", this.value, ""));
            text = stat.getDisplayFormat() == ItemStat.DisplayFormat.MULTIPLIER ? ret + "x" : ret;
        }
        return TextUtil.withColor(new StringTextComponent(text), color);
    }

    private IFormattableTextComponent formatMax(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        String text = trimNumber(String.format(format, "^", this.value));
        return TextUtil.withColor(new StringTextComponent(text), Color.WHITE);
    }

    private IFormattableTextComponent formatMul1(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        int percent = Math.round(100 * this.value);
        Color color = getFormattedColor(percent, 0f, addColor);
        String text = trimNumber(String.format("%s%d%%", percent < 0 ? "" : "+", percent));
        return TextUtil.withColor(new StringTextComponent(text), color);
    }

    private IFormattableTextComponent formatMul2(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        float val = 1f + this.value;
        Color color = getFormattedColor(val, 1f, addColor);
        String text = trimNumber(String.format(format, "x", val));
        return TextUtil.withColor(new StringTextComponent(text), color);
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

    public boolean shouldList(IGearPart part, ItemStat stat, boolean advanced) {
        return shouldList(part.getType(), stat, advanced);
    }

    public boolean shouldList(PartType partType, ItemStat stat, boolean advanced) {
        return advanced || value != 0 || (partType == PartType.MAIN && stat == ItemStats.HARVEST_LEVEL);
    }

    public int getPreferredDecimalPlaces(ItemStat stat, int max) {
        return stat.isDisplayAsInt() && op != Operation.MUL1 && op != Operation.MUL2 ? 0 : 2;
    }

    @Override
    public String toString() {
        return String.format("StatInstance{value=%f, op=%s}", this.value, this.op);
    }

    public JsonElement serialize(IItemStat stat) {
        if (op == Operation.AVG) {
            return new JsonPrimitive(this.value);
        }
        JsonObject json = new JsonObject();
        json.addProperty(this.op.name().toLowerCase(Locale.ROOT), this.value);
        return json;
    }

    public static StatInstance read(ItemStat stat, JsonElement json) {
        return read(stat, stat.getDefaultOperation(), json);
    }

    public static StatInstance read(ItemStat stat, Operation defaultOp, JsonElement json) {
        if (json.isJsonPrimitive()) {
            // Primitive default op shorthand
            return new StatInstance(json.getAsFloat(), defaultOp);
        } else if (json.isJsonObject()) {
            // Either a specified op shorthand or classic format
            JsonObject jsonObj = json.getAsJsonObject();
            StatInstance result = readShorthandObject(stat, jsonObj);
            if (result != null) {
                return result;
            } else {
                // Classic format
                float value = JSONUtils.getFloat(jsonObj, "value", 0f);
                Operation op = jsonObj.has("op")
                        ? Operation.byName(JSONUtils.getString(jsonObj, "op"))
                        : defaultOp;
                return new StatInstance(value, op);
            }
        }
        throw new JsonParseException("Expected stat modifier JSON to be float or object");
    }

    @Nullable
    private static StatInstance readShorthandObject(ItemStat stat, JsonObject json) {
        StatInstance result = null;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Operation op = Operation.byNameOrNull(entry.getKey());
            if (op != null) {
                if (result == null) {
                    result = new StatInstance(entry.getValue().getAsFloat(), op);
                } else {
                    // Found multiple ops in the object. This does not make sense!
                    throw new JsonParseException("Found multiple op keys in stat modifier object");
                }
            }
        }
        return result;
    }

    public static StatInstance read(PacketBuffer buffer) {
        float value = buffer.readFloat();
        Operation op = buffer.readEnumValue(Operation.class);
        return new StatInstance(value, op);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeFloat(this.value);
        buffer.writeEnumValue(this.op);
    }
}

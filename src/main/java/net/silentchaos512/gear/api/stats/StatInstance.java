package net.silentchaos512.gear.api.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.utils.EnumUtils;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
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
        AVG, ADD, MUL1, MUL2, MAX;

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

    public static final StatInstance ZERO = new StatInstance(0f, Operation.ADD);

    private static final Pattern REGEX_TRIM_TO_INT = Pattern.compile("\\.0+$");
    private static final Pattern REGEX_REMOVE_TRAILING_ZEROS = Pattern.compile("0+$");

    private final float value;
    private final Operation op;

    @Deprecated
    public StatInstance(String id, float value, Operation op) {
        this.value = value;
        this.op = op;
    }

    public StatInstance(float value, Operation op) {
        this.value = value;
        this.op = op;
    }

    public StatInstance copy() {
        return new StatInstance(this.value, this.op);
    }

    /**
     * Get the ID of the stat modifier. ID's are used to filter duplicate modifiers in {@link
     * StatModifierMap} and for debugging purposes.
     *
     * @return The modifier ID
     * @deprecated Will remove in 1.16
     */
    @Deprecated
    public String getId() {
        return "DEPRECATED";
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

    public static StatInstance makeBaseMod(float value) {
        return new StatInstance("_base_mod", value, Operation.ADD);
    }

    public static StatInstance makeGearMod(float multi) {
        return new StatInstance("_gear_mod", multi, Operation.MUL1);
    }

    @Deprecated
    public StatInstance copyAppendId(String append) {
        return copyWithNewId(this.getId() + append);
    }

    @Deprecated
    public StatInstance copyWithNewId(String newId) {
        return new StatInstance(newId, this.value, this.op);
    }

    public String formattedString(@Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f") + "%s";
        TextFormatting color;

        switch (this.op) {
            case ADD:
                color = getFormattedColor(this.value, 0f, addColor);
                return trimNumber(color + String.format(format, this.value < 0 ? "" : "+", this.value, ""));
            case AVG:
                return trimNumber(String.format(format, "", this.value, ""));
            case MAX:
                return trimNumber(String.format(format, "^", this.value, ""));
            case MUL1:
                int percent = Math.round(100 * this.value);
                color = getFormattedColor(percent, 0f, addColor);
                return trimNumber(color + String.format("%s%d%%", percent < 0 ? "" : "+", percent));
            case MUL2:
                float val = 1f + this.value;
                color = getFormattedColor(val, 1f, addColor);
                return trimNumber(color + String.format(format, "x", val, ""));
            default:
                throw new NotImplementedException("Unknown operation: " + op);
        }
    }

    private static String trimNumber(CharSequence str) {
        // Trim number to an int if possible, or just trim off any trailing zeros
        String trimToInt = REGEX_TRIM_TO_INT.matcher(str).replaceFirst("");
        if (trimToInt.contains("."))
            return REGEX_REMOVE_TRAILING_ZEROS.matcher(trimToInt).replaceFirst("");
        return trimToInt;
    }

    private TextFormatting getFormattedColor(float val, float whiteVal, boolean addColor) {
        if (!addColor) return TextFormatting.WHITE;
        return val < whiteVal ? TextFormatting.RED : val == whiteVal ? TextFormatting.WHITE : TextFormatting.GREEN;
    }

    public boolean shouldList(IGearPart part, ItemStat stat, boolean advanced) {
        return advanced || value != 0 || (part.getType() == PartType.MAIN && stat == ItemStats.HARVEST_LEVEL);
    }

    public int getPreferredDecimalPlaces(ItemStat stat, int max) {
        return stat.isDisplayAsInt() && op != Operation.MUL1 && op != Operation.MUL2 ? 0 : 2;
    }

    @Override
    public String toString() {
        return String.format("StatInstance{value=%f, op=%s}", this.value, this.op);
    }

    public static StatInstance read(IGearPart part, ItemStat stat, JsonElement json) {
        if (json.isJsonPrimitive()) {
            // Primitive default op shorthand
            return new StatInstance(json.getAsFloat(), part.getDefaultStatOperation(stat));
        } else if (json.isJsonObject()) {
            // Either a specified op shorthand or classic format
            JsonObject jsonObj = json.getAsJsonObject();
            StatInstance result = readShorthandObject(part, stat, jsonObj);
            if (result != null) {
                return result;
            } else {
                // Classic format
                float value = JSONUtils.getFloat(jsonObj, "value", 0f);
                Operation op = jsonObj.has("op")
                        ? Operation.byName(JSONUtils.getString(jsonObj, "op"))
                        : part.getDefaultStatOperation(stat);
                return new StatInstance(value, op);
            }
        }
        throw new JsonParseException("Expected stat modifier JSON to be float or object");
    }

    @Nullable
    private static StatInstance readShorthandObject(IGearPart part, ItemStat stat, JsonObject json) {
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

    @Deprecated
    public static StatInstance read(String id, PacketBuffer buffer) {
        float value = buffer.readFloat();
        Operation op = EnumUtils.byOrdinal(buffer.readByte(), Operation.AVG);
        return new StatInstance(id, value, op);
    }

    public static StatInstance read(PacketBuffer buffer) {
        float value = buffer.readFloat();
        Operation op = EnumUtils.byOrdinal(buffer.readByte(), Operation.AVG);
        return new StatInstance(value, op);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeFloat(this.value);
        buffer.writeByte(this.op.ordinal());
    }
}

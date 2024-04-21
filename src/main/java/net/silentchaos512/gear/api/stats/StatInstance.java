package net.silentchaos512.gear.api.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.*;
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

    public static final StatGearKey DEFAULT_KEY = StatGearKey.of(() -> SilentGear.getId("null"), GearType.ALL);
    private static final Pattern REGEX_TRIM_TO_INT = Pattern.compile("\\.0+$");
    private static final Pattern REGEX_REMOVE_TRAILING_ZEROS = Pattern.compile("0+$");

    final float value;
    final Operation op;
    final StatGearKey key;

    protected StatInstance(float value, Operation op, StatGearKey key) {
        this.value = value;
        this.op = op;
        this.key = key;
    }

    @Deprecated
    public static StatInstance of(float value) {
        return of(value, Operation.AVG, DEFAULT_KEY);
    }

    @Deprecated
    public static StatInstance of(float value, Operation op) {
        return of(value, op, DEFAULT_KEY);
    }

    public static StatInstance of(float value, Operation op, StatGearKey key) {
        return new StatInstance(value, op, key);
    }

    @Deprecated
    public static StatInstance withSource(float value, String source) {
        return withSource(value, Operation.AVG, source);
    }

    @Deprecated
    public static StatInstance withSource(float value, Operation op, String source) {
        return withSource(value, op, DEFAULT_KEY, source);
    }

    public static StatInstance withSource(float value, Operation op, StatGearKey key, String source) {
        return new StatInstanceWithSource(value, op, key, source);
    }

    public StatInstance copySetValue(float newValue) {
        return of(newValue, this.op, this.key);
    }

    public StatInstance copy() {
        return new StatInstance(this.value, this.op, this.key);
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

    public StatGearKey getKey() {
        return key;
    }

    public String getSource() {
        return "N/A";
    }

    public static StatInstance getWeightedAverageMod(Collection<StatInstance> modifiers, Operation op) {
        float value = ItemStat.getWeightedAverage(modifiers, op);
        StatGearKey key = getMostSpecificKey(modifiers);
        return new StatInstance(value, op, key);
    }

    public static StatInstance getMaterialWeightedAverageMod(Collection<StatInstance> modifiers, Operation op) {
        float value = ItemStat.getMaterialWeightedAverage(modifiers, op);
        StatGearKey key = getMostSpecificKey(modifiers);
        return new StatInstance(value, op, key);
    }

    private static StatGearKey getMostSpecificKey(Collection<StatInstance> modifiers) {
        // Gets the key furthest down the gear type hierarchy (key with most parents)
        Set<StatGearKey> found = new HashSet<>();
        for (StatInstance mod : modifiers) {
            found.add(mod.key);
        }

        StatGearKey ret = null;
        int best = 0;

        for (StatGearKey key : found) {
            int parents = 0;
            StatGearKey parent = key.getParent();

            while (parent != null) {
                parent = parent.getParent();
                ++parents;
            }

            if (parents > best || ret == null) {
                best = parents;
                ret = key;
            }
        }

        return ret != null ? ret : DEFAULT_KEY;
    }

    public MutableComponent getFormattedText(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
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

    private MutableComponent formatAdd(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        Color color = getFormattedColor(this.value, 0f, addColor);
        String text = trimNumber(String.format(format, this.value < 0 ? "" : "+", this.value));
        return TextUtil.withColor(Component.literal(text), color);
    }

    private MutableComponent formatAvg(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        Color color = getFormattedColor(this.value, 0f, addColor);
        String text;
        if (stat.getDisplayFormat() == ItemStat.DisplayFormat.PERCENTAGE) {
            text = Math.round(this.value * 100) + "%";
        } else {
            // v (or vx for multiplier stats like armor durability)
            String format = "%s" + ("%." + decimalPlaces + "f") + "%s";
            String ret = trimNumber(String.format(format, "", this.value, ""));
            text = stat.getDisplayFormat() == ItemStat.DisplayFormat.MULTIPLIER ? ret + "x" : ret;
        }
        return TextUtil.withColor(Component.literal(text), color);
    }

    private MutableComponent formatMax(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        String text = trimNumber(String.format(format, "\u2191", this.value));
        return TextUtil.withColor(Component.literal(text), Color.WHITE);
    }

    private MutableComponent formatMul1(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        int percent = Math.round(100 * this.value);
        Color color = getFormattedColor(percent, 0f, addColor);
        String text = trimNumber(String.format("%s%d%%", percent < 0 ? "" : "+", percent));
        return TextUtil.withColor(Component.literal(text), color);
    }

    private MutableComponent formatMul2(ItemStat stat, @Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f");
        float val = 1f + this.value;
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

    public boolean shouldList(IGearPart part, ItemStat stat, boolean advanced) {
        return shouldList(part.getType(), stat, advanced);
    }

    public boolean shouldList(PartType partType, ItemStat stat, boolean advanced) {
        return advanced || value != 0;
    }

    public int getPreferredDecimalPlaces(ItemStat stat, int max) {
        return stat.isDisplayAsInt() && op != Operation.MUL1 && op != Operation.MUL2 ? 0 : 2;
    }

    @Override
    public String toString() {
        return String.format("StatInstance{value=%.3f, op=%s, key=%s}", this.value, this.op, this.key);
    }

    public JsonElement serialize() {
        if (op == Operation.AVG) {
            return new JsonPrimitive(this.value);
        }
        JsonObject json = new JsonObject();
        json.addProperty(this.op.name().toLowerCase(Locale.ROOT), this.value);
        return json;
    }

    public static StatInstance read(StatGearKey key, JsonElement json) {
        Operation defaultOp = key.getStat().getDefaultOperation();
        if (json.isJsonPrimitive()) {
            // Primitive default op shorthand
            return new StatInstance(json.getAsFloat(), defaultOp, key);
        } else if (json.isJsonObject()) {
            // Either a specified op shorthand or classic format
            JsonObject jsonObj = json.getAsJsonObject();
            StatInstance result = readShorthandObject(key, jsonObj);
            if (result != null) {
                return result;
            } else {
                // Classic format
                float value = GsonHelper.getAsFloat(jsonObj, "value", 0f);
                Operation op = jsonObj.has("op")
                        ? Operation.byName(GsonHelper.getAsString(jsonObj, "op"))
                        : defaultOp;
                return new StatInstance(value, op, key);
            }
        }
        throw new JsonParseException("Expected stat modifier JSON to be float or object");
    }

    @Nullable
    private static StatInstance readShorthandObject(StatGearKey key, JsonObject json) {
        StatInstance result = null;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Operation op = Operation.byNameOrNull(entry.getKey());
            if (op != null) {
                if (result == null) {
                    result = new StatInstance(entry.getValue().getAsFloat(), op, key);
                } else {
                    // Found multiple ops in the object. This does not make sense!
                    throw new JsonParseException("Found multiple op keys in stat modifier object");
                }
            }
        }
        return result;
    }

    public static StatInstance read(@Nullable StatGearKey key, FriendlyByteBuf buffer) {
        float value = buffer.readFloat();
        Operation op = buffer.readEnum(Operation.class);
        return new StatInstance(value, op, key != null ? key : DEFAULT_KEY);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.value);
        buffer.writeEnum(this.op);
    }
}

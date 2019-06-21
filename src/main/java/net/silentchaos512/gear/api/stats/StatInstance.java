package net.silentchaos512.gear.api.stats;

import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.utils.EnumUtils;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnegative;

/**
 * Represents either a stat modifier or a calculated stat value.
 *
 * @author SilentChaos512
 * @since Experimental
 */
@Getter
public class StatInstance {
    public enum Operation {
        AVG, ADD, MUL1, MUL2, MAX;

        public static Operation byName(String str) {
            for (Operation op : values())
                if (op.name().equalsIgnoreCase(str))
                    return op;
            return AVG;
        }
    }

    public static final StatInstance ZERO = new StatInstance("__zero__", 0f, StatInstance.Operation.ADD);

    private final String id;
    private final float value;
    private final StatInstance.Operation op;

    public StatInstance(String id, float value, StatInstance.Operation op) {
        this.id = id;
        this.value = value;
        this.op = op;
    }

    public static StatInstance makeBaseMod(float value) {
        return new StatInstance("_base_mod", value, Operation.ADD);
    }

    public static StatInstance makeGearMod(float multi) {
        return new StatInstance("_gear_mod", multi, Operation.MUL1);
    }

    public StatInstance copyAppendId(String append) {
        return copyWithNewId(this.id + append);
    }

    public StatInstance copyWithNewId(String newId) {
        return new StatInstance(newId, this.value, this.op);
    }

    public String formattedString(@Nonnegative int decimalPlaces, boolean addColor) {
        String format = "%s" + ("%." + decimalPlaces + "f") + "%s";
        TextFormatting color;

        switch (this.op) {
            case ADD:
                color = getFormattedColor(this.value, 0f, addColor);
                return color + String.format(format, this.value < 0 ? "" : "+", this.value, "");
            case AVG:
                return String.format(format, "", this.value, "");
            case MAX:
                return String.format(format, "^", this.value, "");
            case MUL1:
                int percent = Math.round(100 * this.value);
                color = getFormattedColor(percent, 0f, addColor);
                format = "%s%d%%";
                return color + String.format(format, percent < 0 ? "" : "+", percent);
            case MUL2:
                float val = 1f + this.value;
                color = getFormattedColor(val, 1f, addColor);
                return color + String.format(format, "x", val, "");
            default:
                throw new NotImplementedException("Unknown operation: " + op);
        }
    }

    private TextFormatting getFormattedColor(float val, float whiteVal, boolean addColor) {
        if (!addColor) return TextFormatting.WHITE;
        return val < whiteVal ? TextFormatting.RED : val == whiteVal ? TextFormatting.WHITE : TextFormatting.GREEN;
    }

    public boolean shouldList(IGearPart part, ItemStat stat, boolean advanced) {
        return advanced || value != 0 || (part.getType() == PartType.MAIN && stat == ItemStats.HARVEST_LEVEL);
    }

    @Override
    public String toString() {
        return String.format("StatInstance{%s, value=%f, op=%s}", this.id, this.value, this.op);
    }

    public static StatInstance read(String id, PacketBuffer buffer) {
        float value = buffer.readFloat();
        Operation op = EnumUtils.byOrdinal(buffer.readByte(), Operation.AVG);
        return new StatInstance(id, value, op);
    }

    public void write(PacketBuffer buffer) {
        buffer.writeFloat(this.value);
        buffer.writeByte(this.op.ordinal());
    }
}

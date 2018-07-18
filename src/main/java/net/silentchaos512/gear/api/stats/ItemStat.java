package net.silentchaos512.gear.api.stats;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A stat that any ICoreItem can use. See {@link CommonItemStats} for stats that can be used.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public class ItemStat {

    public static Map<String, ItemStat> ALL_STATS = new LinkedHashMap<>();

    @Getter(value = AccessLevel.PUBLIC)
    protected final String unlocalizedName;
    @Getter(value = AccessLevel.PUBLIC)
    protected final float defaultValue;
    @Getter(value = AccessLevel.PUBLIC)
    protected final float minimumValue;
    @Getter(value = AccessLevel.PUBLIC)
    protected final float maximumValue;
    // TODO: Hide hidden stats!
    private boolean isHidden = false;
    private boolean synergyApplies = false;
    private boolean affectedByGrades = true;

    public final boolean displayAsInt;
    public final TextFormatting displayColor;

    public ItemStat(String unlocalizedName, float defaultValue, float minValue, float maxValue, boolean displayAsInt, TextFormatting displayColor) {
        this.unlocalizedName = unlocalizedName;
        this.defaultValue = defaultValue;
        this.minimumValue = minValue;
        this.maximumValue = maxValue;

        this.displayAsInt = displayAsInt;
        this.displayColor = displayColor;

        if (minimumValue > maximumValue) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        } else if (defaultValue < minimumValue) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (defaultValue > maximumValue) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }

        ALL_STATS.put(unlocalizedName, this);
    }

    public float clampValue(float value) {
        value = MathHelper.clamp(value, minimumValue, maximumValue);
        return value;
    }

    private static final float WEIGHT_BASE_MIN = 2f;
    private static final float WEIGHT_BASE_MAX = 40f;
    private static final float WEIGHT_DEVIATION_COEFF = 2f;

    public float compute(float baseValue, Collection<StatInstance> modifiers) {
//    if (this == CommonItemStats.DURABILITY) {
//      SilentGear.log.debug("Modifiers for " + this.unlocalizedName);
//      for (StatInstance inst : modifiers)
//        SilentGear.log.debug("    " + inst);
//    }

        if (modifiers.isEmpty())
            return baseValue;

        // Used for weighted average. Percent difference in the value between each part and the primary part affects
        // weight. The bigger the difference, the less weight the part has.
        float primaryMod = -1f;
        for (StatInstance mod : modifiers) {
            if (mod.getOp() == StatInstance.Operation.AVG) {
                if (primaryMod < 0f) {
                    primaryMod = mod.getValue();
                }
            }
        }
        if (primaryMod <= 0f)
            primaryMod = 1f;

        float f0 = baseValue;

        // Average (weighted, used for mains)
        int count = 0;
        float totalWeight = 0f;
        for (StatInstance mod : modifiers) {
            if (mod.getOp() == StatInstance.Operation.AVG) {
                ++count;
                float weightBase = MathHelper.clamp(WEIGHT_BASE_MIN + WEIGHT_DEVIATION_COEFF
                        * (mod.getValue() - primaryMod) / primaryMod, WEIGHT_BASE_MIN, WEIGHT_BASE_MAX);
                float weight = (float) Math.pow(weightBase, -(count == 0 ? count : 0.5 + 0.5f * count));
                totalWeight += weight;
                f0 += mod.getValue() * weight;
            }
        }
        if (count > 0)
            f0 /= totalWeight;

        // Additive
        for (StatInstance mod : modifiers)
            if (mod.getOp() == StatInstance.Operation.ADD)
                f0 += mod.getValue();

        // Multiplicative
        float f1 = f0;
        for (StatInstance mod : modifiers)
            if (mod.getOp() == StatInstance.Operation.MUL1)
                f1 += f0 * mod.getValue();

        // Multiplicative2
        for (StatInstance mod : modifiers)
            if (mod.getOp() == StatInstance.Operation.MUL2)
                f1 *= 1.0f + mod.getValue();

        // Maximum
        for (StatInstance mod : modifiers)
            if (mod.getOp() == StatInstance.Operation.MAX)
                f1 = Math.max(f1, mod.getValue());

        return clampValue(f1);
    }

    public StatInstance computeForDisplay(float baseValue, Collection<StatInstance> modifiers) {
        if (modifiers.isEmpty())
            return new StatInstance("no_mods", baseValue, Operation.AVG);

        int add = 1;
        for (StatInstance inst : modifiers) {
            if (inst.getOp() == Operation.AVG || inst.getOp() == Operation.ADD) {
                add = 0;
                break;
            }
        }

        float value = compute(baseValue + add, modifiers) - add;
        Operation op = modifiers.iterator().next().getOp();
        return new StatInstance("display_" + this.unlocalizedName, value, op);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public ItemStat setHidden(boolean value) {
        this.isHidden = value;
        return this;
    }

    public boolean doesSynergyApply() {
        return synergyApplies;
    }

    public ItemStat setSynergyApplies(boolean value) {
        this.synergyApplies = value;
        return this;
    }

    public boolean isAffectedByGrades() {
        return affectedByGrades;
    }

    public ItemStat setAffectedByGrades(boolean value) {
        this.affectedByGrades = value;
        return this;
    }

    public String toString() {
        return String.format("ItemStat{%s, default=%.2f, min=%.2f, max=%.2f}", unlocalizedName, defaultValue, minimumValue, maximumValue);
    }
}

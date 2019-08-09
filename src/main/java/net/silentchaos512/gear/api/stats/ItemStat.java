package net.silentchaos512.gear.api.stats;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A stat that any ICoreItem can use. See {@link ItemStats} for stats that can be used.
 *
 * @author SilentChaos512
 * @since Experimental
 */
@ParametersAreNonnullByDefault
public class ItemStat {
    public static final Map<String, ItemStat> ALL_STATS = new LinkedHashMap<>();

    @Getter protected final ResourceLocation name;
    @Getter protected final float defaultValue;
    @Getter protected final float minimumValue;
    @Getter protected final float maximumValue;
    Function<Float, Float> missingRodFunction;
    // TODO: Hide hidden stats!
    private boolean isHidden = false;
    private boolean synergyApplies = false;
    private boolean affectedByGrades = true;

    public final boolean displayAsInt;
    public final TextFormatting displayColor;

    public ItemStat(ResourceLocation name, float defaultValue, float minValue, float maxValue, boolean displayAsInt, TextFormatting displayColor) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.minimumValue = minValue;
        this.maximumValue = maxValue;

        this.displayAsInt = displayAsInt;
        this.displayColor = displayColor;

        if (this.minimumValue > this.maximumValue) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        } else if (this.defaultValue < this.minimumValue) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (this.defaultValue > this.maximumValue) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }

        ALL_STATS.put(name.getPath(), this);
    }

    public float clampValue(float value) {
        value = MathHelper.clamp(value, minimumValue, maximumValue);
        return value;
    }

    private static final float WEIGHT_BASE_MIN = 2f;
    private static final float WEIGHT_BASE_MAX = 40f;
    private static final float WEIGHT_DEVIATION_COEFF = 2f;

    public float compute(float baseValue, Collection<StatInstance> modifiers) {
        return compute(baseValue, true, modifiers);
    }

    public float compute(float baseValue, boolean clampValue, Collection<StatInstance> modifiers) {
        if (modifiers.isEmpty())
            return baseValue;

        // Used for weighted average. Percent difference in the value between each part and the primary part affects
        // weight. The bigger the difference, the less weight the part has.
        final float primaryMod = getPrimaryMod(modifiers);

        float f0 = baseValue;

        // Average (weighted, used for mains)
        int count = 0;
        float totalWeight = 0f;
        for (StatInstance mod : modifiers) {
            if (mod.getOp() == StatInstance.Operation.AVG) {
                ++count;
                float weight = getModifierWeight(mod, primaryMod, count);
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

        return clampValue ? clampValue(f1) : f1;
    }

    private static float getPrimaryMod(Iterable<StatInstance> modifiers) {
        float primaryMod = -1f;
        for (StatInstance mod : modifiers) {
            if (mod.getOp() == StatInstance.Operation.AVG) {
                if (primaryMod < 0f) {
                    primaryMod = mod.getValue();
                }
            }
        }
        return primaryMod > 0 ? primaryMod : 1;
    }

    private static float getModifierWeight(StatInstance mod, float primaryMod, int count) {
        float weightBase = WEIGHT_BASE_MIN + WEIGHT_DEVIATION_COEFF * (mod.getValue() - primaryMod) / primaryMod;
        float weightBaseClamped = MathHelper.clamp(weightBase, WEIGHT_BASE_MIN, WEIGHT_BASE_MAX);
        return (float) Math.pow(weightBaseClamped, -(count == 0 ? count : 0.5 + 0.5f * count));
    }

    public StatInstance computeForDisplay(float baseValue, MaterialGrade grade, Collection<StatInstance> modifiers) {
        if (modifiers.isEmpty())
            return new StatInstance("no_mods", baseValue, Operation.AVG);

        int add = 1;
        for (StatInstance inst : modifiers) {
            Operation op = inst.getOp();
            if (op == Operation.AVG || op == Operation.ADD || op == Operation.MAX) {
                add = 0;
                break;
            }
        }

        float value = compute(baseValue + add, false, modifiers) - add;
        if (affectedByGrades) {
            // FIXME: This doesn't exactly match the calculations done in GearData
            float gradeBonus = 1f + grade.bonusPercent / 100f;
            value *= gradeBonus;
        }
        Operation op = modifiers.iterator().next().getOp();
        return new StatInstance("display_" + this.name, value, op);
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

    public float withMissingRodEffect(float statValue) {
        if (missingRodFunction == null) return statValue;
        return missingRodFunction.apply(statValue);
    }

    public ItemStat setMissingRodEffect(Function<Float, Float> function) {
        this.missingRodFunction = function;
        return this;
    }

    public String toString() {
        return String.format("ItemStat{%s, default=%.2f, min=%.2f, max=%.2f}", name, defaultValue, minimumValue, maximumValue);
    }

    @Deprecated
    public String translatedName() {
        return I18n.format("stat." + name.getNamespace() + "." + name.getPath());
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("stat." + name.getNamespace() + "." + name.getPath());
    }
}

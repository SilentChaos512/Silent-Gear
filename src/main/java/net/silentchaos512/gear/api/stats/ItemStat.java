package net.silentchaos512.gear.api.stats;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.function.Function;

/**
 * A stat that any ICoreItem can use. See {@link ItemStats} for stats that can be used.
 * <p>
 * TODO: Rename to GearStat in 2.0
 *
 * @author SilentChaos512
 * @since Experimental
 */
@ParametersAreNonnullByDefault
public class ItemStat extends ForgeRegistryEntry<ItemStat> {
    private final float defaultValue;
    private final float minimumValue;
    private final float maximumValue;
    private final Operation defaultOperation;
    private final TextFormatting nameColor;
    private final boolean visible;
    private final boolean synergyApplies;
    private final boolean displayAsInt;
    private final Function<Float, Float> missingRodFunction;

    public ItemStat(float defaultValue, float minValue, float maxValue, TextFormatting nameColor, Properties properties) {
        this.defaultValue = defaultValue;
        this.minimumValue = minValue;
        this.maximumValue = maxValue;
        this.nameColor = nameColor;

        this.defaultOperation = properties.defaultOp;
        this.displayAsInt = properties.displayAsInt;
        this.visible = properties.visible;
        this.synergyApplies = properties.synergyApplies;
        this.missingRodFunction = properties.missingRodFunction;

        if (this.minimumValue > this.maximumValue) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        } else if (this.defaultValue < this.minimumValue) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (this.defaultValue > this.maximumValue) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }

        ItemStats.STATS_IN_ORDER.add(this);
    }

    /**
     * @return The stat name
     * @deprecated Use {@link #getRegistryName()} instead
     */
    @Deprecated
    public ResourceLocation getName() {
        return getRegistryName();
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float getMinimumValue() {
        return minimumValue;
    }

    public float getMaximumValue() {
        return maximumValue;
    }

    public Operation getDefaultOperation() {
        return defaultOperation;
    }

    public boolean isDisplayAsInt() {
        return displayAsInt;
    }

    public TextFormatting getNameColor() {
        return nameColor;
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

    @SuppressWarnings("OverlyComplexMethod")
    public float compute(float baseValue, boolean clampValue, Collection<StatInstance> modifiers) {
        if (modifiers.isEmpty())
            return baseValue;

        float f0 = baseValue;

        // Average (weighted, used for mains)
        f0 += getWeightedAverage(modifiers, Operation.AVG);

        // Maximum
        for (StatInstance mod : modifiers)
            if (mod.getOp() == StatInstance.Operation.MAX)
                f0 = Math.max(f0, mod.getValue());

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

        return clampValue ? clampValue(f1) : f1;
    }

    private static float getPrimaryMod(Iterable<StatInstance> modifiers, Operation op) {
        float primaryMod = -1f;
        for (StatInstance mod : modifiers) {
            if (mod.getOp() == op) {
                if (primaryMod < 0f) {
                    primaryMod = mod.getValue();
                }
            }
        }
        return primaryMod > 0 ? primaryMod : 1;
    }

    public static float getWeightedAverage(Collection<StatInstance> modifiers, Operation op) {
        float primaryMod = getPrimaryMod(modifiers, op);
        float ret = 0;
        int count = 0;
        float totalWeight = 0f;
        for (StatInstance mod : modifiers) {
            if (mod.getOp() == op) {
                ++count;
                float weight = getModifierWeight(mod, primaryMod, count);
                totalWeight += weight;
                ret += mod.getValue() * weight;
            }
        }
        return count > 0 ? ret / totalWeight : ret;
    }

    private static float getModifierWeight(StatInstance mod, float primaryMod, int count) {
        float weightBase = WEIGHT_BASE_MIN + WEIGHT_DEVIATION_COEFF * (mod.getValue() - primaryMod) / primaryMod;
        float weightBaseClamped = MathHelper.clamp(weightBase, WEIGHT_BASE_MIN, WEIGHT_BASE_MAX);
        return (float) Math.pow(weightBaseClamped, -(count == 0 ? count : 0.5 + 0.5f * count));
    }

    @Deprecated
    public StatInstance computeForDisplay(float baseValue, MaterialGrade grade, Collection<StatInstance> modifiers) {
        return computeForDisplay(baseValue, modifiers);
    }

    public StatInstance computeForDisplay(float baseValue, Collection<StatInstance> modifiers) {
        if (modifiers.isEmpty())
            return new StatInstance(baseValue, Operation.AVG);

        int add = 1;
        for (StatInstance inst : modifiers) {
            Operation op = inst.getOp();
            if (op == Operation.AVG || op == Operation.ADD || op == Operation.MAX) {
                add = 0;
                break;
            }
        }

        float value = compute(baseValue + add, false, modifiers) - add;
        Operation op = modifiers.iterator().next().getOp();
        return new StatInstance(value, op);
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean doesSynergyApply() {
        return synergyApplies;
    }

    public float withMissingRodEffect(float statValue) {
        if (missingRodFunction == null) return statValue;
        return missingRodFunction.apply(statValue);
    }

    public String toString() {
        return String.format("ItemStat{%s, default=%.2f, min=%.2f, max=%.2f}", getRegistryName(), defaultValue, minimumValue, maximumValue);
    }

    public ITextComponent getDisplayName() {
        ResourceLocation name = getRegistryName();
        if (name == null)
            return new StringTextComponent("Unregistered stat: " + this);
        return new TranslationTextComponent("stat." + name.getNamespace() + "." + name.getPath());
    }

    @SuppressWarnings("WeakerAccess")
    public static class Properties {
        private Operation defaultOp = Operation.AVG;
        private boolean displayAsInt;
        private boolean visible = true;
        private boolean synergyApplies = false;
        private Function<Float, Float> missingRodFunction;

        public Properties defaultOp(Operation op) {
            this.defaultOp = op;
            return this;
        }

        public Properties displayAsInt() {
            displayAsInt = true;
            return this;
        }

        public Properties hidden() {
            visible = false;
            return this;
        }

        public Properties synergyApplies() {
            synergyApplies = true;
            return this;
        }

        public Properties missingRodFunction(Function<Float, Float> function) {
            missingRodFunction = function;
            return this;
        }
    }
}

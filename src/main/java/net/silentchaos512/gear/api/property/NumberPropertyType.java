package net.silentchaos512.gear.api.property;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.api.item.GearType;

import java.util.Collection;

public class NumberPropertyType extends GearPropertyType<Float, NumberProperty> {
    public enum DisplayFormat {
        UNIT, MULTIPLIER, PERCENTAGE
    }

    private static final Codec<NumberProperty> FULL_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("value").forGetter(NumberProperty::value),
                    PropertyOp.CODEC.optionalFieldOf("operation", PropertyOp.AVERAGE).forGetter(NumberProperty::operation)
            ).apply(instance, NumberProperty::new)
    );
    public static final Codec<NumberProperty> CODEC = Codec.either(Codec.FLOAT, FULL_CODEC)
            .xmap(
                    either -> either.map(f -> new NumberProperty(f, PropertyOp.AVERAGE), p -> p),
                    property -> {
                        if (property.operation() == PropertyOp.AVERAGE) {
                            return Either.left(property.value());
                        }
                        return Either.right(property);
                    }
            );

    private final PropertyOp defaultOperation;
    private final DisplayFormat displayFormat;
    private final boolean displayAsInt;

    public NumberPropertyType(PropertyOp defaultOperation, DisplayFormat displayFormat, boolean displayAsInt, Builder<Float> builder) {
        super(builder);
        this.defaultOperation = defaultOperation;
        this.displayFormat = displayFormat;
        this.displayAsInt = displayAsInt;

        if (this.minimumValue > this.maximumValue) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        } else if (this.defaultValue < this.minimumValue) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (this.defaultValue > this.maximumValue) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    @Override
    public Codec<NumberProperty> codec() {
        return CODEC;
    }

    public DisplayFormat getDisplayFormat() {
        return displayFormat;
    }

    public boolean isDisplayAsInt() {
        return displayAsInt;
    }

    public float clampValue(float value) {
        value = Mth.clamp(value, minimumValue, maximumValue);
        return value;
    }

    @Override
    public Float compute(Float baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<NumberProperty> modifiers) {
        if (modifiers.isEmpty())
            return baseValue;

        float f0 = baseValue;

        // Average (weighted, used for mains)
        f0 += getWeightedAverage(modifiers, PropertyOp.AVERAGE);

        // Maximum
        for (NumberProperty mod : modifiers)
            if (mod.operation() == PropertyOp.MAX)
                f0 = Math.max(f0, mod.value());

        // Multiplicative
        float f1 = f0;
        for (NumberProperty mod : modifiers)
            if (mod.operation() == PropertyOp.MULTIPLY_BASE)
                f1 += f0 * mod.value();

        // Multiplicative2
        for (NumberProperty mod : modifiers)
            if (mod.operation() == PropertyOp.MULTIPLY_TOTAL)
                f1 *= 1.0f + mod.value();

        // Additive
        for (NumberProperty mod : modifiers)
            if (mod.operation() == PropertyOp.ADD)
                f1 += mod.value();

        return clampResult ? clampValue(f1) : f1;
    }

    private static float getPrimaryMod(Iterable<NumberProperty> modifiers, PropertyOp op) {
        float primaryMod = -1f;
        for (NumberProperty mod : modifiers) {
            if (mod.operation() == op) {
                if (primaryMod < 0f) {
                    primaryMod = mod.value();
                }
            }
        }
        return primaryMod > 0 ? primaryMod : 1;
    }

    public static float getWeightedAverage(Collection<NumberProperty> modifiers, PropertyOp op) {
        float primaryMod = getPrimaryMod(modifiers, op);
        float ret = 0;
        float totalWeight = 0f;
        for (NumberProperty mod : modifiers) {
            if (mod.operation() == op) {
                float weight = getModifierWeight(mod, primaryMod);
                totalWeight += weight;
                ret += mod.value() * weight;
            }
        }
        return totalWeight > 0 ? ret / totalWeight : ret;
    }

    private static float getModifierWeight(NumberProperty mod, float primaryMod) {
        return 1f + mod.value / (1f + Math.abs(primaryMod));
    }

    @Override
    public MutableComponent getFormattedText(NumberProperty value, int decimalPlaces, boolean addColor) {
        return value.operation().formatNumberValue(this, value.value, decimalPlaces, addColor);
    }
}

package net.silentchaos512.gear.api.property;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nonnegative;
import java.util.*;
import java.util.regex.Pattern;

public class NumberProperty extends GearProperty<Float, NumberPropertyValue> {
    public enum DisplayFormat {
        UNIT, MULTIPLIER, PERCENTAGE
    }

    private static final Codec<NumberPropertyValue> FULL_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("value").forGetter(NumberPropertyValue::value),
                    Operation.CODEC.optionalFieldOf("operation").forGetter(v -> Optional.of(v.operation()))
            ).apply(instance, (value, operation) -> new NumberPropertyValue(value, operation.orElse(Operation.AVERAGE)))
    );
    public static final Codec<NumberPropertyValue> CODEC = Codec.either(Codec.FLOAT, FULL_CODEC)
            .xmap(
                    either -> either.map(f -> new NumberPropertyValue(f, Operation.AVERAGE), p -> p),
                    property -> {
                        if (property.operation() == Operation.AVERAGE) {
                            return Either.left(property.value());
                        }
                        return Either.right(property);
                    }
            );

    public static final StreamCodec<FriendlyByteBuf, NumberPropertyValue> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, NumberPropertyValue::value,
            Operation.STREAM_CODEC, NumberPropertyValue::operation,
            NumberPropertyValue::new
    );

    private final Operation defaultOperation;
    private final DisplayFormat displayFormat;
    private final boolean displayAsInt;

    public NumberProperty(Operation defaultOperation, DisplayFormat displayFormat, boolean displayAsInt, Builder<Float> builder) {
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
    public Codec<NumberPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, NumberPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public Float getZeroValue() {
        return 0f;
    }

    @Override
    public boolean isZero(Float value) {
        return MathUtils.floatsEqual(value, 0f);
    }

    @Override
    public NumberPropertyValue valueOf(Float value) {
        return new NumberPropertyValue(value, Operation.AVERAGE);
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
    public Float compute(Float baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<NumberPropertyValue> modifiers) {
        if (modifiers.isEmpty())
            return baseValue;

        float f0 = baseValue;

        // Average (weighted, used for mains)
        f0 += getWeightedAverage(modifiers, Operation.AVERAGE);

        // Maximum
        for (NumberPropertyValue mod : modifiers)
            if (mod.operation() == Operation.MAX)
                f0 = Math.max(f0, mod.value());

        // Multiplicative
        float f1 = f0;
        for (NumberPropertyValue mod : modifiers)
            if (mod.operation() == Operation.MULTIPLY_BASE)
                f1 += f0 * mod.value();

        // Multiplicative2
        for (NumberPropertyValue mod : modifiers)
            if (mod.operation() == Operation.MULTIPLY_TOTAL)
                f1 *= 1.0f + mod.value();

        // Additive
        for (NumberPropertyValue mod : modifiers)
            if (mod.operation() == Operation.ADD)
                f1 += mod.value();

        return clampResult ? clampValue(f1) : f1;
    }

    @Override
    public List<NumberPropertyValue> compressModifiers(Collection<NumberPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        var result = new ArrayList<NumberPropertyValue>();
        for (var operation : Operation.values()) {
            var modsForOp = modifiers.stream().filter(m -> m.operation() == operation).toList();
            if (modsForOp.size() > 1) {
                result.add(compressModifiers(modsForOp, operation));
            } else if (modsForOp.size() == 1) {
                result.add(modsForOp.getFirst());
            }
        }
        return result;
    }

    private static NumberPropertyValue compressModifiers(Collection<NumberPropertyValue> mods, NumberProperty.Operation operation) {
        // We do NOT want to average together max modifiers...
        if (operation == NumberProperty.Operation.MAX) {
            return mods.stream()
                    .max((o1, o2) -> Float.compare(o1.value(), o2.value()))
                    .orElse(new NumberPropertyValue(0, operation));
        }

        float primaryMod = getPrimaryMod(mods, operation);
        float ret = 0;
        float totalWeight = 0f;
        for (var mod : mods) {
            if (mod.operation() == operation) {
                float weight = 1f + mod.value() / (1f + Math.abs(primaryMod));
                totalWeight += weight;
                ret += mod.value() * weight;
            }
        }
        var value = totalWeight > 0 ? ret / totalWeight : ret;
        return new NumberPropertyValue(value, operation);
    }

    private static float getPrimaryMod(Iterable<NumberPropertyValue> modifiers, Operation op) {
        float primaryMod = -1f;
        for (NumberPropertyValue mod : modifiers) {
            if (mod.operation() == op) {
                if (primaryMod < 0f) {
                    primaryMod = mod.value();
                }
            }
        }
        return primaryMod > 0 ? primaryMod : 1;
    }

    public static float getWeightedAverage(Collection<NumberPropertyValue> modifiers, Operation op) {
        float primaryMod = getPrimaryMod(modifiers, op);
        float ret = 0;
        float totalWeight = 0f;
        for (NumberPropertyValue mod : modifiers) {
            if (mod.operation() == op) {
                float weight = getModifierWeight(mod, primaryMod);
                totalWeight += weight;
                ret += mod.value() * weight;
            }
        }
        return totalWeight > 0 ? ret / totalWeight : ret;
    }

    private static float getModifierWeight(NumberPropertyValue mod, float primaryMod) {
        return 1f + mod.value / (1f + Math.abs(primaryMod));
    }

    @Override
    public NumberPropertyValue applySynergy(NumberPropertyValue value, float synergy) {
        var multiplier = synergy - 1f;
        var newNumberValue = value.value() + Math.abs(value.value()) * multiplier;
        return new NumberPropertyValue(newNumberValue, value.operation());
    }

    @Override
    public MutableComponent formatValueWithColor(NumberPropertyValue value, boolean addColor) {
        return value.operation().formatNumberValue(this, value.value, getPreferredDecimalPlaces(value), addColor);
    }

    @Override
    public int getPreferredDecimalPlaces(NumberPropertyValue value) {
        var isMultiply = value.operation() == Operation.MULTIPLY_BASE || value.operation() == Operation.MULTIPLY_TOTAL;
        return this.isDisplayAsInt() && !isMultiply ? 0 : 2;
    }

    @Override
    public List<NumberPropertyValue> sortForDisplay(Collection<NumberPropertyValue> mods) {
        var list = new ArrayList<>(mods);
        list.sort(Comparator.comparing(mod -> mod.operation().ordinal()));
        return list;
    }

    @Override
    public Component formatValue(NumberPropertyValue value) {
        return value.operation().formatNumberValue(this, value.value, getPreferredDecimalPlaces(value), false);
    }

    public enum Operation {
        AVERAGE("avg"),
        MAX("max"),
        ADD("add"),
        MULTIPLY_BASE("mul1"),
        MULTIPLY_TOTAL("mul2");

        public static final Codec<Operation> CODEC = Codec.STRING.comapFlatMap(
                s -> {
                    for (Operation op : values()) {
                        if (s.equalsIgnoreCase(op.name()) || s.equalsIgnoreCase(op.alias)) {
                            return DataResult.success(op);
                        }
                    }
                    return DataResult.error(() -> "Unknown operation: " + s);
                },
                Enum::name
        );
        public static final StreamCodec<FriendlyByteBuf, Operation> STREAM_CODEC = StreamCodec.of(
                (buf, op) -> buf.writeVarInt(op.ordinal()),
                buf -> Operation.values()[buf.readVarInt()]
        );

        private final String alias;

        Operation(String alias) {
            this.alias = alias;
        }

        public MutableComponent formatNumberValue(NumberProperty property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
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

        private MutableComponent formatAdd(NumberProperty property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
            String format = "%s" + ("%." + decimalPlaces + "f");
            Color color = getFormattedColor(value, 0f, addColor);
            String text = trimNumber(String.format(format, value < 0 ? "" : "+", value));
            return TextUtil.withColor(Component.literal(text), color);
        }

        private MutableComponent formatAvg(NumberProperty property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
            Color color = getFormattedColor(value, 0f, addColor);
            String text;
            if (property.getDisplayFormat() == DisplayFormat.PERCENTAGE) {
                text = Math.round(value * 100) + "%";
            } else {
                // v (or vx for multiplier stats like armor durability)
                String format = "%s" + ("%." + decimalPlaces + "f") + "%s";
                String ret = trimNumber(String.format(format, "", value, ""));
                text = property.getDisplayFormat() == DisplayFormat.MULTIPLIER ? ret + "x" : ret;
            }
            return TextUtil.withColor(Component.literal(text), color);
        }

        private MutableComponent formatMax(NumberProperty property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
            String format = "%s" + ("%." + decimalPlaces + "f");
            String text = trimNumber(String.format(format, "↑", value)); //u2191
            return TextUtil.withColor(Component.literal(text), Color.WHITE);
        }

        private MutableComponent formatMul1(NumberProperty property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
            int percent = Math.round(100 * value);
            Color color = getFormattedColor(percent, 0f, addColor);
            String text = trimNumber(String.format("%s%d%%", percent < 0 ? "" : "+", percent));
            return TextUtil.withColor(Component.literal(text), color);
        }

        private MutableComponent formatMul2(NumberProperty property, float value, @Nonnegative int decimalPlaces, boolean addColor) {
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
}

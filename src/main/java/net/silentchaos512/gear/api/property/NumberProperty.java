package net.silentchaos512.gear.api.property;

import java.util.regex.Pattern;

public final class NumberProperty extends GearProperty<Float> {
    private final PropertyOp operation;

    private static final Pattern REGEX_TRIM_TO_INT = Pattern.compile("\\.0+$");
    private static final Pattern REGEX_REMOVE_TRAILING_ZEROS = Pattern.compile("0+$");

    public NumberProperty(float value, PropertyOp operation) {
        super(value);
        this.operation = operation;
    }

    public PropertyOp operation() {
        return this.operation;
    }

    public int getPreferredDecimalPlaces(NumberPropertyType propertyType) {
        var isMultiply = operation == PropertyOp.MULTIPLY_BASE || operation == PropertyOp.MULTIPLY_TOTAL;
        return propertyType.isDisplayAsInt() && !isMultiply ? 0 : 2;
    }
}

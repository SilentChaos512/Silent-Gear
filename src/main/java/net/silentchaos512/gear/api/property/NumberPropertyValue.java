package net.silentchaos512.gear.api.property;

import net.silentchaos512.gear.setup.gear.GearPropertyTypes;

public final class NumberPropertyValue extends GearPropertyValue<Float> {
    private final NumberProperty.Operation operation;

    public NumberPropertyValue(float value, NumberProperty.Operation operation) {
        super(value);
        this.operation = operation;
    }

    public NumberProperty.Operation operation() {
        return this.operation;
    }

    @Override
    public GearPropertyType<?> type() {
        return GearPropertyTypes.NUMBER.get();
    }

    public static NumberPropertyValue average(float value) {
        return new NumberPropertyValue(value, NumberProperty.Operation.AVERAGE);
    }
}

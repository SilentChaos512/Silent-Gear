package net.silentchaos512.gear.api.property;

public class BooleanPropertyValue extends GearPropertyValue<Boolean> {
    public BooleanPropertyValue(boolean value) {
        super(value);
    }

    @Override
    public String toString() {
        return this.value ? "True" : "False";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof BooleanPropertyValue other)) return false;

        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return this.value ? 1 : 0;
    }
}

package net.silentchaos512.gear.api.property;

public abstract class GearPropertyValue<T> {
    protected final T value;

    public GearPropertyValue(T value) {
        this.value = value;
    }

    public T value() {
        return this.value;
    }
}

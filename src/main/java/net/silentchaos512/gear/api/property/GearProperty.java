package net.silentchaos512.gear.api.property;

public abstract class GearProperty<V> {
    protected final V value;

    public GearProperty(V value) {
        this.value = value;
    }

    public V value() {
        return this.value;
    }
}

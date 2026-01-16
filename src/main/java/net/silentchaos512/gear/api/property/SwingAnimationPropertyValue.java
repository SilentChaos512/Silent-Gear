package net.silentchaos512.gear.api.property;

import net.minecraft.world.item.component.SwingAnimation;

public class SwingAnimationPropertyValue extends GearPropertyValue<SwingAnimation> {
    public SwingAnimationPropertyValue(SwingAnimation value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof SwingAnimationPropertyValue other)) return false;

        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}

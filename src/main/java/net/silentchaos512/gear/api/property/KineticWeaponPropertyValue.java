package net.silentchaos512.gear.api.property;

import net.minecraft.world.item.component.KineticWeapon;

public class KineticWeaponPropertyValue extends GearPropertyValue<KineticWeapon> {
    public KineticWeaponPropertyValue(KineticWeapon value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof KineticWeaponPropertyValue other)) return false;

        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}

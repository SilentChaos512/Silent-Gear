package net.silentchaos512.gear.gear;

import net.minecraft.resources.Identifier;

public class TraitJsonException extends GearJsonException {
    public TraitJsonException(Identifier resourceName, String packName, Throwable cause) {
        super(resourceName, packName, cause);
    }
}

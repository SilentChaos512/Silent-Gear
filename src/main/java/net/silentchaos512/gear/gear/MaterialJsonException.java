package net.silentchaos512.gear.gear;

import net.minecraft.resources.Identifier;

public class MaterialJsonException extends GearJsonException {
    public MaterialJsonException(Identifier resourceName, String packName, Throwable cause) {
        super(resourceName, packName, cause);
    }
}

package net.silentchaos512.gear.gear;

import net.minecraft.resources.Identifier;

public class PartJsonException extends GearJsonException {
    public PartJsonException(Identifier resourceName, String packName, Throwable cause) {
        super(resourceName, packName, cause);
    }
}

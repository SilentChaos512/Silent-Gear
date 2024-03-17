package net.silentchaos512.gear.gear;

import net.minecraft.resources.ResourceLocation;

public class PartJsonException extends GearJsonException {
    public PartJsonException(ResourceLocation resourceName, String packName, Throwable cause) {
        super(resourceName, packName, cause);
    }
}

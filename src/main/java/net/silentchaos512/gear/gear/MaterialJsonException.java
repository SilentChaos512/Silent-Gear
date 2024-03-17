package net.silentchaos512.gear.gear;

import net.minecraft.resources.ResourceLocation;

public class MaterialJsonException extends GearJsonException {
    public MaterialJsonException(ResourceLocation resourceName, String packName, Throwable cause) {
        super(resourceName, packName, cause);
    }
}

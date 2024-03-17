package net.silentchaos512.gear.gear;

import net.minecraft.resources.ResourceLocation;

public class TraitJsonException extends GearJsonException {
    public TraitJsonException(ResourceLocation resourceName, String packName, Throwable cause) {
        super(resourceName, packName, cause);
    }
}

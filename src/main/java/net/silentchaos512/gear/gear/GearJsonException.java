package net.silentchaos512.gear.gear;

import net.minecraft.resources.Identifier;

public class GearJsonException extends RuntimeException{
    public GearJsonException(Identifier resourceName, String packName, Throwable cause) {
        super("Error loading \"" + resourceName + "\" from pack \"" + packName + "\": " + cause.getMessage(), cause);
    }
}

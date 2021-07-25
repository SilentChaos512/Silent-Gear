package net.silentchaos512.gear.util;

import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public class ModResourceLocation extends ResourceLocation {
    public ModResourceLocation(String resourceName) {
        super(addModNamespace(resourceName));
    }

    private static String addModNamespace(String resourceName) {
        if (resourceName.contains(":")) {
            return resourceName;
        }
        return SilentGear.MOD_ID + ":" + resourceName;
    }
}

package net.silentchaos512.gear.parts;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public class PartConst {
    public static final ResourceLocation FALLBACK_BOWSTRING = get("bowstring/string");
    public static final ResourceLocation FALLBACK_MAIN = get("main/iron");
    public static final ResourceLocation FALLBACK_ROD = get("rod/wood");
    public static final ResourceLocation MISC_SPOON = get("misc/spoon");

    private static ResourceLocation get(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}

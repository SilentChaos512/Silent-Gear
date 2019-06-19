package net.silentchaos512.gear.parts;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public final class PartConst {
    public static final ResourceLocation FALLBACK_BOWSTRING = SilentGear.getId("bowstring/string");
    public static final ResourceLocation FALLBACK_MAIN = SilentGear.getId("main/iron");
    public static final ResourceLocation FALLBACK_ROD = SilentGear.getId("rod/wood");

    public static final ResourceLocation MISC_SPOON = SilentGear.getId("misc/spoon");

    private PartConst() {
        throw new IllegalAccessError("Utility class");
    }
}

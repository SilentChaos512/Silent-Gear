package net.silentchaos512.gear.parts;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.util.DataResource;

public final class PartConst {
    public static final ResourceLocation FALLBACK_BOWSTRING = SilentGear.getId("bowstring/string");
    public static final ResourceLocation FALLBACK_MAIN = SilentGear.getId("main/iron");
    public static final ResourceLocation FALLBACK_ROD = SilentGear.getId("rod/wood");

    public static final ResourceLocation BOWSTRING_EXAMPLE = SilentGear.getId("bowstring/example");
    public static final ResourceLocation MAIN_EXAMPLE = SilentGear.getId("main/example");
    public static final ResourceLocation ROD_EXAMPLE = SilentGear.getId("rod/example");

    public static final ResourceLocation HIGHLIGHT = SilentGear.getId("highlight/highlight");
    public static final ResourceLocation ROUGH_ROD = SilentGear.getId("rod/rough");

    public static final DataResource<IGearPart> ARMOR_BODY = DataResource.part("armor_body");

    public static final DataResource<IGearPart> MISC_SPOON = DataResource.part("misc/spoon");
    public static final DataResource<IGearPart> RED_CARD = DataResource.part("misc/red_card");

    private PartConst() {
        throw new IllegalAccessError("Utility class");
    }
}

package net.silentchaos512.gear.client.model;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartTextures {
    private static final List<ResourceLocation> ALL = new ArrayList<>();

    public static final ResourceLocation HIGHLIGHT = get("_highlight");
    public static final ResourceLocation MAIN_GENERIC_LC = get("main_generic_lc");
    public static final ResourceLocation MAIN_GENERIC_HC = get("main_generic_hc");
    public static final ResourceLocation ROD_GENERIC_LC = get("rod_generic_lc");
    public static final ResourceLocation ROD_GENERIC_HC = get("rod_generic_hc");
    public static final ResourceLocation TIP_IRON = get("tip_iron");
    public static final ResourceLocation BINDING_GENERIC = get("binding_generic");
    public static final ResourceLocation GRIP_WOOL = get("grip_wool");
    public static final ResourceLocation BOWSTRING_STRING = get("bowstring_string");
    public static final ResourceLocation FLETCHING_GENERIC = get("fletching_generic");

    private PartTextures() {}

    private static ResourceLocation get(String path) {
        ResourceLocation ret = SilentGear.getId(path);
        ALL.add(ret);
        return ret;
    }

    public static List<ResourceLocation> getAllTextures() {
        return Collections.unmodifiableList(ALL);
    }
}

package net.silentchaos512.gear.client.model;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class PartTextures {
    private static final Map<ResourceLocation, GearType> ALL = new HashMap<>();

    public static final ResourceLocation HIGHLIGHT = get("_highlight", GearType.TOOL);
    public static final ResourceLocation MAIN_GENERIC_LC = get("main_generic_lc", GearType.TOOL);
    public static final ResourceLocation MAIN_GENERIC_HC = get("main_generic_hc", GearType.TOOL);
    public static final ResourceLocation ROD_GENERIC_LC = get("rod_generic_lc", GearType.TOOL);
    public static final ResourceLocation ROD_GENERIC_HC = get("rod_generic_hc", GearType.TOOL);
    public static final ResourceLocation TIP_IRON = get("tip_iron", GearType.TOOL);
    public static final ResourceLocation BINDING_GENERIC = get("binding_generic", GearType.TOOL);
    public static final ResourceLocation GRIP_WOOL = get("grip_wool", GearType.TOOL);
    public static final ResourceLocation BOWSTRING_STRING = get("bowstring_string", GearType.RANGED_WEAPON);
    public static final ResourceLocation ARROW = get("arrow", GearType.RANGED_WEAPON);
    public static final ResourceLocation FLETCHING_GENERIC = get("fletching_generic", null);

    private PartTextures() {}

    private static ResourceLocation get(String path, @Nullable GearType gearType) {
        ResourceLocation ret = SilentGear.getId(path);
        if (gearType != null) {
            ALL.put(ret, gearType);
        }
        return ret;
    }

    public static List<ResourceLocation> getTextures(GearType gearType) {
        return ALL.keySet().stream()
                .filter(tex -> gearType.matches(ALL.get(tex)))
                .collect(Collectors.toList());
    }
}

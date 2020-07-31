package net.silentchaos512.gear.util;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;

public final class Const {
    // Example material. We don't need references to most mats, but this one acts as a placeholder in some cases
    public static final DataResource<IMaterial> EXAMPLE_MATERIAL = DataResource.material("example");
    public static final DataResource<IMaterial> ROUGH_WOOD_MATERIAL = DataResource.material("wood/rough");

    public static final ResourceLocation SALVAGING = SilentGear.getId("salvaging");
    public static final ResourceLocation SALVAGING_COMPOUND_PART = SilentGear.getId("salvaging/compound_part");
    public static final ResourceLocation SALVAGING_GEAR = SilentGear.getId("salvaging/gear");
    public static final ResourceLocation SMITHING_COATING = SilentGear.getId("smithing/coating");

    public static final ResourceLocation GEAR_MODEL_LOADER = SilentGear.getId("gear_model");
    public static final ResourceLocation COMPOUND_PART_MODEL_LOADER = SilentGear.getId("compound_part_model");

    private Const() {}
}

package net.silentchaos512.gear.util;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.parts.IGearPart;

public final class Const {
    public static final ResourceLocation SALVAGING = SilentGear.getId("salvaging");
    public static final ResourceLocation SALVAGING_COMPOUND_PART = SilentGear.getId("salvaging/compound_part");
    public static final ResourceLocation SALVAGING_GEAR = SilentGear.getId("salvaging/gear");
    public static final ResourceLocation SMITHING_COATING = SilentGear.getId("smithing/coating");

    public static final ResourceLocation GEAR_MODEL_LOADER = SilentGear.getId("gear_model");
    public static final ResourceLocation COMPOUND_PART_MODEL_LOADER = SilentGear.getId("compound_part_model");

    public static final class Materials {
        public static final DataResource<IMaterial> AZURE_SILVER = DataResource.material("azure_silver");
        public static final DataResource<IMaterial> EMERALD = DataResource.material("emerald");
        public static final DataResource<IMaterial> EXAMPLE = DataResource.material("example");
        public static final DataResource<IMaterial> WOOD_ROUGH = DataResource.material("wood/rough");

        private Materials() {}
    }

    public static final class Parts {
        public static final DataResource<IGearPart> ARMOR_BODY = DataResource.part("armor_body");

        private Parts() {}
    }

    private Const() {}
}

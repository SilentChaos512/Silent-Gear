package net.silentchaos512.gear.traits.conditions;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.GearHelper;

import java.util.List;

public class GearTypeTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("gear_type");

    private final String gearType;

    public GearTypeTraitCondition(String gearType) {
        this.gearType = gearType;
    }

    public GearTypeTraitCondition(GearType gearType) {
        this.gearType = gearType.getName();
    }

    @Override
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public boolean matches(ItemStack gear, PartDataList parts, ITrait trait) {
        GearType type = GearHelper.getType(gear);
        return type.matches(this.gearType);
    }

    @Override
    public boolean matches(ItemStack gear, PartType partType, List<MaterialInstance> materials, ITrait trait) {
        GearType type = GearHelper.getType(gear);
        return type.matches(this.gearType);
    }

    public static class Serializer implements ITraitConditionSerializer<GearTypeTraitCondition> {

        @Override
        public ResourceLocation getId() {
            return GearTypeTraitCondition.NAME;
        }

        @Override
        public GearTypeTraitCondition deserialize(JsonObject json) {
            return new GearTypeTraitCondition(JSONUtils.getString(json, "gear_type"));
        }

        @Override
        public void serialize(GearTypeTraitCondition value, JsonObject json) {
            json.addProperty("gear_type", value.gearType);
        }
    }
}

package net.silentchaos512.gear.traits.conditions;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;

public class MaterialRatioTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("material_ratio");

    private final float requiredRatio;

    public MaterialRatioTraitCondition(float requiredRatio) {
        this.requiredRatio = requiredRatio;
    }

    @Override
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public boolean matches(ItemStack gear, PartDataList parts, ITrait trait) {
        // FIXME: Make it work with the new material system
        float ratio = (float) parts.getPartsWithTrait(trait) / parts.getMains().size();
        return ratio >= this.requiredRatio;
    }

    public static class Serializer implements ITraitConditionSerializer<MaterialRatioTraitCondition> {

        @Override
        public ResourceLocation getId() {
            return MaterialRatioTraitCondition.NAME;
        }

        @Override
        public MaterialRatioTraitCondition deserialize(JsonObject json) {
            return new MaterialRatioTraitCondition(JSONUtils.getFloat(json, "ratio"));
        }

        @Override
        public void serialize(MaterialRatioTraitCondition value, JsonObject json) {
            json.addProperty("ratio", value.requiredRatio);
        }
    }
}

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

public class MaterialCountTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("material_count");

    private final int requiredCount;

    public MaterialCountTraitCondition(int requiredCount) {
        this.requiredCount = requiredCount;
    }

    @Override
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public boolean matches(ItemStack gear, PartDataList parts, ITrait trait) {
        // FIXME: Make it work with the new material system
        int count = parts.getPartsWithTrait(trait);
        return count >= requiredCount;
    }

    public static class Serializer implements ITraitConditionSerializer<MaterialCountTraitCondition> {

        @Override
        public ResourceLocation getId() {
            return MaterialCountTraitCondition.NAME;
        }

        @Override
        public MaterialCountTraitCondition deserialize(JsonObject json) {
            return new MaterialCountTraitCondition(JSONUtils.getInt(json, "count"));
        }

        @Override
        public void serialize(MaterialCountTraitCondition value, JsonObject json) {
            json.addProperty("count", value.requiredCount);
        }
    }
}

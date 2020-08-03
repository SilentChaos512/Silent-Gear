package net.silentchaos512.gear.traits.conditions;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.List;

public class PrimaryMaterialTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("primary_material");

    @Override
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public boolean matches(ItemStack gear, PartDataList parts, ITrait trait) {
        return false;
    }

    @Override
    public boolean matches(ItemStack gear, PartType partType, List<MaterialInstance> materials, ITrait trait) {
        return !materials.isEmpty() && materials.get(0).getMaterial().getTraits(partType).stream().anyMatch(t -> t.getTrait() == trait);
    }

    public static class Serializer implements ITraitConditionSerializer<PrimaryMaterialTraitCondition> {

        @Override
        public ResourceLocation getId() {
            return PrimaryMaterialTraitCondition.NAME;
        }

        @Override
        public PrimaryMaterialTraitCondition deserialize(JsonObject json) {
            return new PrimaryMaterialTraitCondition();
        }

        @Override
        public void serialize(PrimaryMaterialTraitCondition value, JsonObject json) {
        }
    }
}

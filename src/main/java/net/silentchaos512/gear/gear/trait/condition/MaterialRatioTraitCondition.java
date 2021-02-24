package net.silentchaos512.gear.gear.trait.condition;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collection;
import java.util.List;

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
    public ITraitConditionSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(ItemStack gear, GearType gearType, PartDataList parts, ITrait trait) {
        float ratio = (float) parts.getPartsWithTrait(trait) / parts.getMains().size();
        return ratio >= this.requiredRatio;
    }

    @Override
    public boolean matches(ItemStack gear, GearType gearType, PartType partType, List<MaterialInstance> materials, ITrait trait) {
        int count = 0;
        for (MaterialInstance mat : materials) {
            for (TraitInstance inst : mat.getTraits(partType, gearType, gear)) {
                if (inst.getTrait() == trait) {
                    count++;
                    break;
                }
            }
        }
        float ratio = (float) count / materials.size();
        return ratio >= this.requiredRatio;
    }

    @Override
    public boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<IGearComponentInstance<?>> components) {
        int count = 0;
        for (IGearComponentInstance<?> comp : components) {
            Collection<TraitInstance> traits = comp.getTraits(key, gear);
            for (TraitInstance inst : traits) {
                if (inst.getTrait() == trait) {
                    ++count;
                    break;
                }
            }
        }
        float ratio = (float) count / components.size();
        return ratio >= this.requiredRatio;
    }

    @Override
    public IFormattableTextComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "material_ratio", Math.round(this.requiredRatio * 100));
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

        @Override
        public MaterialRatioTraitCondition read(PacketBuffer buffer) {
            float ratio = buffer.readFloat();
            return new MaterialRatioTraitCondition(ratio);
        }

        @Override
        public void write(MaterialRatioTraitCondition condition, PacketBuffer buffer) {
            buffer.writeFloat(condition.requiredRatio);
        }
    }
}

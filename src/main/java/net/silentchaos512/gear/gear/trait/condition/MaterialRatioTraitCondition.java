package net.silentchaos512.gear.gear.trait.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
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
    public boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<? extends IGearComponentInstance<?>> components) {
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
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "material_ratio", Math.round(this.requiredRatio * 100));
    }

    public static class Serializer implements ITraitConditionSerializer<MaterialRatioTraitCondition> {
        @Override
        public ResourceLocation getId() {
            return MaterialRatioTraitCondition.NAME;
        }

        @Override
        public MaterialRatioTraitCondition deserialize(JsonObject json) {
            return new MaterialRatioTraitCondition(GsonHelper.getAsFloat(json, "ratio"));
        }

        @Override
        public void serialize(MaterialRatioTraitCondition value, JsonObject json) {
            json.addProperty("ratio", value.requiredRatio);
        }

        @Override
        public MaterialRatioTraitCondition read(FriendlyByteBuf buffer) {
            float ratio = buffer.readFloat();
            return new MaterialRatioTraitCondition(ratio);
        }

        @Override
        public void write(MaterialRatioTraitCondition condition, FriendlyByteBuf buffer) {
            buffer.writeFloat(condition.requiredRatio);
        }
    }
}

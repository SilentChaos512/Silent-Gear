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

import java.util.List;

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
    public ITraitConditionSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<? extends IGearComponentInstance<?>> components) {
        int count = 0;
        for (IGearComponentInstance<?> comp : components) {
            for (TraitInstance inst : comp.getTraits(key, gear)) {
                if (inst.getTrait() == trait) {
                    count++;
                    break;
                }
            }
        }
        return count >= this.requiredCount;
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "material_count", this.requiredCount);
    }

    public static class Serializer implements ITraitConditionSerializer<MaterialCountTraitCondition> {
        @Override
        public ResourceLocation getId() {
            return MaterialCountTraitCondition.NAME;
        }

        @Override
        public MaterialCountTraitCondition deserialize(JsonObject json) {
            return new MaterialCountTraitCondition(GsonHelper.getAsInt(json, "count"));
        }

        @Override
        public void serialize(MaterialCountTraitCondition value, JsonObject json) {
            json.addProperty("count", value.requiredCount);
        }

        @Override
        public MaterialCountTraitCondition read(FriendlyByteBuf buffer) {
            int count = buffer.readByte();
            return new MaterialCountTraitCondition(count);
        }

        @Override
        public void write(MaterialCountTraitCondition condition, FriendlyByteBuf buffer) {
            buffer.writeByte(condition.requiredCount);
        }
    }
}

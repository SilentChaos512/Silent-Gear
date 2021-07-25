package net.silentchaos512.gear.gear.trait.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
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

@Deprecated
public class PrimaryMaterialTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("primary_material");

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
        if (!components.isEmpty()) {
            for (TraitInstance t : components.get(0).getTraits(key, gear)) {
                if (t.getTrait() == trait) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "primary");
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
            // NO-OP
        }

        @Override
        public PrimaryMaterialTraitCondition read(FriendlyByteBuf buffer) {
            return new PrimaryMaterialTraitCondition();
        }

        @Override
        public void write(PrimaryMaterialTraitCondition condition, FriendlyByteBuf buffer) {
            // NO-OP
        }
    }
}

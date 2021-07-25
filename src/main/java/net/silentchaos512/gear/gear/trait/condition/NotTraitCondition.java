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
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public class NotTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("not");

    private final ITraitCondition child;

    public NotTraitCondition(ITraitCondition child) {
        this.child = child;
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
        return !this.child.matches(trait, key, gear, components);
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "not", this.child.getDisplayText());
    }

    public static class Serializer implements ITraitConditionSerializer<NotTraitCondition> {

        @Override
        public ResourceLocation getId() {
            return NotTraitCondition.NAME;
        }

        @Override
        public NotTraitCondition deserialize(JsonObject json) {
            return new NotTraitCondition(TraitSerializers.deserializeCondition(GsonHelper.getAsJsonObject(json, "value")));
        }

        @Override
        public void serialize(NotTraitCondition value, JsonObject json) {
            json.add("value", TraitSerializers.serializeCondition(value.child));
        }

        @Override
        public NotTraitCondition read(FriendlyByteBuf buffer) {
            return new NotTraitCondition(TraitSerializers.readCondition(buffer));
        }

        @Override
        public void write(NotTraitCondition condition, FriendlyByteBuf buffer) {
            TraitSerializers.writeCondition(condition.child, buffer);
        }
    }
}

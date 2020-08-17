package net.silentchaos512.gear.gear.trait.condition;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.trait.TraitSerializers;

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
    public boolean matches(ItemStack gear, PartDataList parts, ITrait trait) {
        return !child.matches(gear, parts, trait);
    }

    @Override
    public boolean matches(ItemStack gear, PartType partType, List<MaterialInstance> materials, ITrait trait) {
        return !child.matches(gear, partType, materials, trait);
    }

    public static class Serializer implements ITraitConditionSerializer<NotTraitCondition> {

        @Override
        public ResourceLocation getId() {
            return NotTraitCondition.NAME;
        }

        @Override
        public NotTraitCondition deserialize(JsonObject json) {
            return new NotTraitCondition(TraitSerializers.deserializeCondition(JSONUtils.getJsonObject(json, "value")));
        }

        @Override
        public void serialize(NotTraitCondition value, JsonObject json) {
            json.add("value", TraitSerializers.serializeCondition(value.child));
        }

        @Override
        public NotTraitCondition read(PacketBuffer buffer) {
            return new NotTraitCondition(TraitSerializers.readCondition(buffer));
        }

        @Override
        public void write(NotTraitCondition condition, PacketBuffer buffer) {
            TraitSerializers.writeCondition(condition.child, buffer);
        }
    }
}

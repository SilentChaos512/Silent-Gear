package net.silentchaos512.gear.gear.trait.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.util.TextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("and");

    private final ITraitCondition[] children;

    public AndTraitCondition(ITraitCondition... values) {
        //noinspection ConstantConditions
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Values must not be empty");
        }

        for (ITraitCondition child : values) {
            if (child == null) {
                throw new IllegalArgumentException("Value must not be null");
            }
        }

        this.children = values.clone();
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
        for (ITraitCondition child : this.children) {
            if (!child.matches(trait, key, gear, components)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MutableComponent getDisplayText() {
        Component text = Arrays.stream(this.children)
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "and")).append(t2))
                .orElseGet(() -> Component.literal(""));
        return Component.literal("(").append(text).append(")");
    }

    public static class Serializer implements ITraitConditionSerializer<AndTraitCondition> {
        @Override
        public ResourceLocation getId() {
            return AndTraitCondition.NAME;
        }

        @Override
        public AndTraitCondition deserialize(JsonObject json) {
            List<ITraitCondition> children = new ArrayList<>();
            for (JsonElement j : GsonHelper.getAsJsonArray(json, "values")) {
                if (!j.isJsonObject()) {
                    throw new JsonSyntaxException("And condition values must be array of objects");
                }
                children.add(TraitSerializers.deserializeCondition(j.getAsJsonObject()));
            }
            return new AndTraitCondition(children.toArray(new ITraitCondition[0]));
        }

        @Override
        public void serialize(AndTraitCondition value, JsonObject json) {
            JsonArray values = new JsonArray();
            for (ITraitCondition c : value.children) {
                values.add(TraitSerializers.serializeCondition(c));
            }
            json.add("values", values);
        }

        @Override
        public AndTraitCondition read(FriendlyByteBuf buffer) {
            List<ITraitCondition> children = new ArrayList<>();
            int count = buffer.readByte();
            for (int i = 0; i < count; ++i) {
                children.add(TraitSerializers.readCondition(buffer));
            }
            return new AndTraitCondition(children.toArray(new ITraitCondition[0]));
        }

        @Override
        public void write(AndTraitCondition condition, FriendlyByteBuf buffer) {
            buffer.writeByte(condition.children.length);
            for (ITraitCondition child : condition.children) {
                TraitSerializers.writeCondition(child, buffer);
            }
        }
    }
}

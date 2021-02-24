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
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public class GearTypeTraitCondition implements ITraitCondition {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ResourceLocation NAME = SilentGear.getId("gear_type");

    private final String gearType;

    public GearTypeTraitCondition(String gearType) {
        this.gearType = gearType;
    }

    public GearTypeTraitCondition(GearType gearType) {
        this.gearType = gearType.getName();
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
        return gear.isEmpty() || gearType.matches(this.gearType);
    }

    @Override
    public boolean matches(ItemStack gear, GearType gearType, PartType partType, List<MaterialInstance> materials, ITrait trait) {
        return gear.isEmpty() || gearType.matches(this.gearType);
    }

    @Override
    public boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<IGearComponentInstance<?>> components) {
        return gear.isEmpty() || key.getGearType().matches(this.gearType);
    }

    @Override
    public IFormattableTextComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "gear_type", this.gearType);
    }

    public static class Serializer implements ITraitConditionSerializer<GearTypeTraitCondition> {
        @Override
        public ResourceLocation getId() {
            return GearTypeTraitCondition.NAME;
        }

        @Override
        public GearTypeTraitCondition deserialize(JsonObject json) {
            return new GearTypeTraitCondition(JSONUtils.getString(json, "gear_type"));
        }

        @Override
        public void serialize(GearTypeTraitCondition value, JsonObject json) {
            json.addProperty("gear_type", value.gearType);
        }

        @Override
        public GearTypeTraitCondition read(PacketBuffer buffer) {
            String gearType = buffer.readString();
            return new GearTypeTraitCondition(gearType);
        }

        @Override
        public void write(GearTypeTraitCondition condition, PacketBuffer buffer) {
            buffer.writeString(condition.gearType);
        }
    }
}

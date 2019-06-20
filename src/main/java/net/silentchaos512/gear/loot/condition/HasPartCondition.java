package net.silentchaos512.gear.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class HasPartCondition implements ILootCondition {
    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation partId;
    private final MaterialGrade.Range gradeRange;

    public HasPartCondition(ResourceLocation partId, MaterialGrade.Range gradeRange) {
        this.partId = partId;
        this.gradeRange = gradeRange;
    }

    @Override
    public boolean test(LootContext context) {
        ItemStack tool = context.get(LootParameters.TOOL);
        if (!GearHelper.isGearNullable(tool)) return false;
        return GearData.hasPart(tool, partId, gradeRange);
    }

    public static ILootCondition.IBuilder builder(ResourceLocation partId) {
        return builder(partId, MaterialGrade.Range.OPEN);
    }

    public static ILootCondition.IBuilder builder(ResourceLocation partId, MaterialGrade.Range gradeRange) {
        return () -> new HasPartCondition(partId, gradeRange);
    }

    public static class Serializer extends AbstractSerializer<HasPartCondition> {
        protected Serializer() {
            super(SilentGear.getId("has_part"), HasPartCondition.class);
        }

        @Override
        public void serialize(JsonObject json, HasPartCondition value, JsonSerializationContext context) {
            json.addProperty("part", value.partId.toString());
        }

        @Override
        public HasPartCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation partId = new ResourceLocation(JSONUtils.getString(json, "part"));
            MaterialGrade.Range gradeRange = json.has("grade")
                    ? MaterialGrade.Range.deserialize(json.get("grade"))
                    : MaterialGrade.Range.OPEN;
            return new HasPartCondition(partId, gradeRange);
        }
    }
}

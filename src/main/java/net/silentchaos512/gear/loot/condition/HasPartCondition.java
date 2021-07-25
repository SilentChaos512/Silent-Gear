package net.silentchaos512.gear.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.init.ModLootStuff;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

@Deprecated // Makes more sense to check for traits
public class HasPartCondition extends GearLootCondition {
    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation partId;

    public HasPartCondition(ResourceLocation partId) {
        this.partId = partId;
    }

    public HasPartCondition(ResourceLocation partId, MaterialGrade.Range gradeRange) {
        this(partId);
    }

    @Override
    public boolean test(LootContext context) {
        ItemStack tool = getItemUsed(context);
        if (!GearHelper.isGear(tool)) return false;

        IGearPart part = PartManager.get(this.partId);
        if (part == null) return false;

        return GearData.hasPart(tool, part);
    }

    public static LootItemCondition.Builder builder(ResourceLocation partId) {
        return builder(partId, MaterialGrade.Range.OPEN);
    }

    public static LootItemCondition.Builder builder(ResourceLocation partId, MaterialGrade.Range gradeRange) {
        return () -> new HasPartCondition(partId, gradeRange);
    }

    @Override
    public LootItemConditionType getType() {
        return ModLootStuff.HAS_PART;
    }

    public static class Serializer implements Serializer<HasPartCondition> {
        @Override
        public void serialize(JsonObject json, HasPartCondition value, JsonSerializationContext context) {
            json.addProperty("part", value.partId.toString());
        }

        @Override
        public HasPartCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation partId = new ResourceLocation(GsonHelper.getAsString(json, "part"));
            MaterialGrade.Range gradeRange = json.has("grade")
                    ? MaterialGrade.Range.deserialize(json.get("grade"))
                    : MaterialGrade.Range.OPEN;
            return new HasPartCondition(partId, gradeRange);
        }
    }
}

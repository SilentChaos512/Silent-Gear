package net.silentchaos512.gear.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.silentchaos512.gear.init.ModLootStuff;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

public class HasTraitCondition extends GearLootCondition {
    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation traitId;
    private final int minLevel;
    private final int maxLevel;

    public HasTraitCondition(ResourceLocation traitId, int minLevel, int maxLevel) {
        this.traitId = traitId;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean test(LootContext context) {
        ItemStack tool = getItemUsed(context);
        if (!GearHelper.isGear(tool)) return false;
        int level = TraitHelper.getTraitLevel(tool, traitId);
        return level >= minLevel && level <= maxLevel;
    }

    public static LootItemCondition.Builder builder(ResourceLocation traitId) {
        return builder(traitId, 1, Integer.MAX_VALUE);
    }

    public static LootItemCondition.Builder builder(ResourceLocation traitId, int minLevel) {
        return builder(traitId, minLevel, Integer.MAX_VALUE);
    }

    public static LootItemCondition.Builder builder(ResourceLocation traitId, int minLevel, int maxLevel) {
        return () -> new HasTraitCondition(traitId, minLevel, maxLevel);
    }

    @Override
    public LootItemConditionType getType() {
        return ModLootStuff.HAS_TRAIT.get();
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<HasTraitCondition> {
        @Override
        public void serialize(JsonObject json, HasTraitCondition value, JsonSerializationContext context) {
            json.addProperty("trait", value.traitId.toString());
        }

        @Override
        public HasTraitCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation traitId = new ResourceLocation(GsonHelper.getAsString(json, "trait"));
            int minLevel = 1;
            int maxLevel = Integer.MAX_VALUE;
            if (json.has("level")) {
                JsonElement levelJson = json.get("level");
                if (levelJson.isJsonPrimitive()) {
                    minLevel = levelJson.getAsInt();
                } else {
                    minLevel = GsonHelper.getAsInt(levelJson.getAsJsonObject(), "min", minLevel);
                    maxLevel = GsonHelper.getAsInt(levelJson.getAsJsonObject(), "max", maxLevel);
                }
            }
            return new HasTraitCondition(traitId, minLevel, maxLevel);
        }
    }
}

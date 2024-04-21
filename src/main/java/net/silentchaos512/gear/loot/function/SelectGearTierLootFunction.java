package net.silentchaos512.gear.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.util.GearGenerator;

public final class SelectGearTierLootFunction extends LootItemConditionalFunction {
    public static final Serializer SERIALIZER = new Serializer();
    private final int tier;

    private SelectGearTierLootFunction(LootItemCondition[] conditions, int tier) {
        super(conditions);
        this.tier = tier;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof ICoreItem)) return stack;
        return GearGenerator.create((ICoreItem) stack.getItem(), this.tier);
    }

    public static LootItemConditionalFunction.Builder<?> builder(int tier) {
        return simpleBuilder(conditions -> new SelectGearTierLootFunction(conditions, tier));
    }

    @Override
    public LootItemFunctionType getType() {
        return SgLoot.SELECT_TIER.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SelectGearTierLootFunction> {
        @Override
        public void serialize(JsonObject object, SelectGearTierLootFunction functionClazz, JsonSerializationContext serializationContext) {
            object.addProperty("tier", functionClazz.tier);
        }

        @Override
        public SelectGearTierLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            int tier = GsonHelper.getAsInt(object, "tier", 2);
            return new SelectGearTierLootFunction(conditionsIn, tier);
        }
    }
}

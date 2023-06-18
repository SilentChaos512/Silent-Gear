package net.silentchaos512.gear.loot.function;

import com.google.gson.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SetPartsFunction extends LootItemConditionalFunction {
    public static final Serializer SERIALIZER = new Serializer();

    private final List<LazyPartData> parts;

    private SetPartsFunction(LootItemCondition[] conditions, List<LazyPartData> parts) {
        super(conditions);
        this.parts = parts;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof ICoreItem)) return stack;
        ItemStack result = stack.copy();
        List<PartData> parts = LazyPartData.createPartList(this.parts);
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result, null);
        parts.forEach(p -> p.onAddToGear(result));
        return result;
    }

    public static LootItemConditionalFunction.Builder<?> builder(List<LazyPartData> parts) {
        return simpleBuilder(conditions -> new SetPartsFunction(conditions, parts));
    }

    @Override
    public LootItemFunctionType getType() {
        return SgLoot.SET_PARTS.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetPartsFunction> {
        @Override
        public void serialize(JsonObject json, SetPartsFunction function, JsonSerializationContext context) {
            super.serialize(json, function, context);
            JsonArray array = new JsonArray();
            function.parts.forEach(part -> array.add(part.serialize()));
            json.add("parts", array);
        }

        @Override
        public SetPartsFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditionsIn) {
            List<LazyPartData> parts = new ArrayList<>();
            JsonArray partsArray = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "parts", new JsonArray()));
            for (JsonElement jsonElement : partsArray) {
                parts.add(LazyPartData.deserialize(jsonElement));
            }
            return new SetPartsFunction(conditionsIn, parts);
        }
    }
}

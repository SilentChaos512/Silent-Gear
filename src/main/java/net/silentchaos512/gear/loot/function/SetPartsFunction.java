package net.silentchaos512.gear.loot.function;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.parts.LazyPartData;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;

import java.util.ArrayList;
import java.util.List;

public final class SetPartsFunction extends LootFunction {
    public static final Serializer SERIALIZER = new Serializer();

    private final List<LazyPartData> parts;

    private SetPartsFunction(ILootCondition[] conditions, List<LazyPartData> parts) {
        super(conditions);
        this.parts = parts;
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof ICoreItem)) return stack;
        ItemStack result = stack.copy();
        List<PartData> parts = LazyPartData.createPartList(this.parts);
        parts.forEach(p -> p.onAddToGear(result));
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result, null);
        return result;
    }

    public static LootFunction.Builder<?> builder(List<LazyPartData> parts) {
        return builder(conditions -> new SetPartsFunction(conditions, parts));
    }

    public static class Serializer extends LootFunction.Serializer<SetPartsFunction> {
        public Serializer() {
            super(SilentGear.getId("set_parts"), SetPartsFunction.class);
        }

        @Override
        public void serialize(JsonObject json, SetPartsFunction function, JsonSerializationContext context) {
            super.serialize(json, function, context);
            JsonArray array = new JsonArray();
            function.parts.forEach(part -> array.add(part.serialize()));
            json.add("parts", array);
        }

        @Override
        public SetPartsFunction deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditionsIn) {
            List<LazyPartData> parts = new ArrayList<>();
            JsonArray partsArray = JSONUtils.getJsonArray(json, "parts", new JsonArray());
            for (JsonElement jsonElement : partsArray) {
                parts.add(LazyPartData.readJson(jsonElement));
            }
            return new SetPartsFunction(conditionsIn, parts);
        }
    }
}

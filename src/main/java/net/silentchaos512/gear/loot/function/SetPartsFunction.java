package net.silentchaos512.gear.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.util.GearData;

import java.util.List;

public final class SetPartsFunction extends LootItemConditionalFunction {
    public static final Codec<SetPartsFunction> CODEC = RecordCodecBuilder.create(
            instance -> commonFields(instance)
                    .and(
                            Codec.list(LazyPartData.CODEC).fieldOf("parts").forGetter(f -> f.parts)
                    )
                    .apply(instance, SetPartsFunction::new)
    );

    private final List<LazyPartData> parts;

    private SetPartsFunction(List<LootItemCondition> conditions, List<LazyPartData> parts) {
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
}

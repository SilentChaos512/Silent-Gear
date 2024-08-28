package net.silentchaos512.gear.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.util.GearData;

import java.util.List;

public final class SetPartsFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetPartsFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance)
                    .and(
                            Codec.list(PartInstance.CODEC).fieldOf("parts").forGetter(f -> f.parts)
                    )
                    .apply(instance, SetPartsFunction::new)
    );

    private final List<PartInstance> parts;

    private SetPartsFunction(List<LootItemCondition> conditions, List<PartInstance> parts) {
        super(conditions);
        this.parts = parts;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof GearItem)) return stack;
        ItemStack result = stack.copy();
        GearData.writeConstructionParts(result, this.parts);
        GearData.recalculateGearData(result, null);
        parts.forEach(p -> p.onAddToGear(result));
        return result;
    }

    public static LootItemConditionalFunction.Builder<?> builder(List<PartInstance> parts) {
        return simpleBuilder(conditions -> new SetPartsFunction(conditions, parts));
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return SgLoot.SET_PARTS.get();
    }
}

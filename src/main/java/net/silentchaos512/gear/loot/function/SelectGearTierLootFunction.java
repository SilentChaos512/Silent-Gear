package net.silentchaos512.gear.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.setup.SgLoot;
import net.silentchaos512.gear.util.GearGenerator;

import java.util.List;

public final class SelectGearTierLootFunction extends LootItemConditionalFunction {
    public static final Codec<SelectGearTierLootFunction> CODEC = RecordCodecBuilder.create(
            instance -> commonFields(instance)
                    .and(
                            Codec.INT.optionalFieldOf("tier", 2).forGetter(f -> f.tier)
                    )
                    .apply(instance, SelectGearTierLootFunction::new)
    );

    private final int tier;

    private SelectGearTierLootFunction(List<LootItemCondition> conditions, int tier) {
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
}

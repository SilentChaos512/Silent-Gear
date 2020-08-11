package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.silentchaos512.gear.init.LootInjector;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

import java.util.function.BiConsumer;

public class ModChestLootTables extends ChestLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(LootInjector.Tables.CHESTS_NETHER_BRIDGE, addNetherMetalsAndFlora());
    }

    private static LootTable.Builder addNetherMetalsAndFlora() {
        LootTable.Builder builder = LootTable.builder();
        addNetherMetals(builder);
        addNetherFlora(builder);
        return builder;
    }

    private static LootTable.Builder addNetherMetalsWithExtra() {
        LootTable.Builder builder = LootTable.builder();
        builder.addLootPool(LootPool.builder()
                .rolls(ConstantRange.of(1))
                .bonusRolls(0, 1)
                .addEntry(ItemLootEntry.builder(CraftingItems.BLAZE_GOLD_INGOT)
                        .weight(6)
                        .acceptFunction(SetCount.builder(RandomValueRange.of(2, 5)))
                )
                .addEntry(ItemLootEntry.builder(CraftingItems.CRIMSON_STEEL_DUST)
                        .weight(1)
                        .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                )
        );
        return addNetherMetals(builder);
    }

    private static LootTable.Builder addNetherMetals(LootTable.Builder builder) {
        builder.addLootPool(LootPool.builder()
                .rolls(RandomValueRange.of(1, 2))
                .addEntry(EmptyLootEntry.func_216167_a()
                        .weight(20)
                )
                .addEntry(ItemLootEntry.builder(CraftingItems.CRIMSON_IRON_INGOT)
                        .weight(35)
                        .acceptFunction(SetCount.builder(RandomValueRange.of(1, 4)))
                )
                .addEntry(ItemLootEntry.builder(CraftingItems.BLAZE_GOLD_NUGGET)
                        .weight(35)
                        .acceptFunction(SetCount.builder(RandomValueRange.of(5, 10)))
                )
                .addEntry(ItemLootEntry.builder(CraftingItems.BLAZE_GOLD_INGOT)
                        .weight(15)
                )
                .addEntry(ItemLootEntry.builder(CraftingItems.CRIMSON_STEEL_INGOT)
                        .weight(1)
                )
        );
        return builder;
    }

    private static LootTable.Builder addNetherFlora(LootTable.Builder builder) {
        builder.addLootPool(LootPool.builder()
                .rolls(RandomValueRange.of(1, 2))
                .bonusRolls(0, 1)
                .addEntry(EmptyLootEntry.func_216167_a()
                        .weight(10)
                )
                .addEntry(ItemLootEntry.builder(ModBlocks.NETHERWOOD_SAPLING)
                        .weight(20)
                        .acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
                )
                .addEntry(ItemLootEntry.builder(ModItems.NETHER_BANANA)
                        .weight(10)
                        .acceptFunction(SetCount.builder(RandomValueRange.of(2, 4)))
                )
                .addEntry(ItemLootEntry.builder(ModItems.GOLDEN_NETHER_BANANA)
                        .weight(1)
                )
        );
        return builder;
    }
}

package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.init.LootInjector;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

import java.util.function.BiConsumer;

public class ModChestLootTables extends ChestLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(LootInjector.Tables.CHESTS_NETHER_BRIDGE, addNetherMetalsAndFlora());
        consumer.accept(LootInjector.Tables.CHESTS_BASTION_TREASURE, addNetherMetalsWithExtra());
        consumer.accept(LootInjector.Tables.CHESTS_BASTION_OTHER, addNetherFlora(LootTable.lootTable()));
        consumer.accept(LootInjector.Tables.CHESTS_BASTION_BRIDGE, addNetherMetalsAndFlora());
        consumer.accept(LootInjector.Tables.CHESTS_RUINED_PORTAL, addNetherMetalsAndFlora());
    }

    private static LootTable.Builder addNetherMetalsAndFlora() {
        LootTable.Builder builder = LootTable.lootTable();
        addNetherMetals(builder);
        addNetherFlora(builder);
        return builder;
    }

    private static LootTable.Builder addNetherMetalsWithExtra() {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantRange.exactly(1))
                .bonusRolls(0, 1)
                .add(ItemLootEntry.lootTableItem(CraftingItems.BLAZE_GOLD_INGOT)
                        .setWeight(6)
                        .apply(SetCount.setCount(RandomValueRange.between(2, 5)))
                )
                .add(ItemLootEntry.lootTableItem(CraftingItems.CRIMSON_STEEL_DUST)
                        .setWeight(1)
                        .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                )
        );
        return addNetherMetals(builder);
    }

    private static LootTable.Builder addNetherMetals(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .setRolls(RandomValueRange.between(1, 2))
                .add(EmptyLootEntry.emptyItem()
                        .setWeight(20)
                )
                .add(ItemLootEntry.lootTableItem(CraftingItems.CRIMSON_IRON_INGOT)
                        .setWeight(35)
                        .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                )
                .add(ItemLootEntry.lootTableItem(CraftingItems.BLAZE_GOLD_NUGGET)
                        .setWeight(35)
                        .apply(SetCount.setCount(RandomValueRange.between(5, 10)))
                )
                .add(ItemLootEntry.lootTableItem(CraftingItems.BLAZE_GOLD_INGOT)
                        .setWeight(15)
                )
                .add(ItemLootEntry.lootTableItem(CraftingItems.CRIMSON_STEEL_INGOT)
                        .setWeight(1)
                )
        );
        return builder;
    }

    private static LootTable.Builder addNetherFlora(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .setRolls(RandomValueRange.between(1, 2))
                .bonusRolls(0, 1)
                .add(EmptyLootEntry.emptyItem()
                        .setWeight(10)
                )
                .add(ItemLootEntry.lootTableItem(ModBlocks.NETHERWOOD_SAPLING)
                        .setWeight(20)
                        .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                )
                .add(ItemLootEntry.lootTableItem(ModItems.NETHER_BANANA)
                        .setWeight(10)
                        .apply(SetCount.setCount(RandomValueRange.between(2, 4)))
                )
                .add(ItemLootEntry.lootTableItem(ModItems.GOLDEN_NETHER_BANANA)
                        .setWeight(1)
                )
        );
        return builder;
    }
}

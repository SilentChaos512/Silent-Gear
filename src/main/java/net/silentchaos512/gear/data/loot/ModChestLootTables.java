package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.ChestLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.silentchaos512.gear.init.LootInjector;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

import java.util.function.BiConsumer;

public class ModChestLootTables extends ChestLoot {
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
                .setRolls(ConstantValue.exactly(1))
                .bonusRolls(0, 1)
                .add(LootItem.lootTableItem(CraftingItems.BLAZE_GOLD_INGOT)
                        .setWeight(6)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                )
                .add(LootItem.lootTableItem(CraftingItems.CRIMSON_STEEL_DUST)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                )
        );
        return addNetherMetals(builder);
    }

    private static LootTable.Builder addNetherMetals(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .add(EmptyLootItem.emptyItem()
                        .setWeight(20)
                )
                .add(LootItem.lootTableItem(CraftingItems.CRIMSON_IRON_INGOT)
                        .setWeight(35)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                )
                .add(LootItem.lootTableItem(CraftingItems.BLAZE_GOLD_NUGGET)
                        .setWeight(35)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 10)))
                )
                .add(LootItem.lootTableItem(CraftingItems.BLAZE_GOLD_INGOT)
                        .setWeight(15)
                )
                .add(LootItem.lootTableItem(CraftingItems.CRIMSON_STEEL_INGOT)
                        .setWeight(1)
                )
        );
        return builder;
    }

    private static LootTable.Builder addNetherFlora(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .bonusRolls(0, 1)
                .add(EmptyLootItem.emptyItem()
                        .setWeight(10)
                )
                .add(LootItem.lootTableItem(ModBlocks.NETHERWOOD_SAPLING)
                        .setWeight(20)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                )
                .add(LootItem.lootTableItem(ModItems.NETHER_BANANA)
                        .setWeight(10)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                )
                .add(LootItem.lootTableItem(ModItems.GOLDEN_NETHER_BANANA)
                        .setWeight(1)
                )
        );
        return builder;
    }
}

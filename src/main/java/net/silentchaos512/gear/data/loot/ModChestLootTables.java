package net.silentchaos512.gear.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgLoot;

import java.util.function.BiConsumer;

public class ModChestLootTables implements LootTableSubProvider {
    public ModChestLootTables(HolderLookup.Provider provider) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, SgLoot.Injector.Tables.NETHER_BRIDGE),
                addNetherMetalsAndFlora()
        );
        consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, SgLoot.Injector.Tables.BASTION_TREASURE),
                addNetherMetalsWithExtra()
        );
        consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, SgLoot.Injector.Tables.BASTION_OTHER),
                addNetherFlora(LootTable.lootTable())
        );
        consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, SgLoot.Injector.Tables.BASTION_BRIDGE),
                addNetherMetalsAndFlora()
        );
        consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, SgLoot.Injector.Tables.RUINED_PORTAL),
                addNetherMetalsAndFlora()
        );
    }

    public static LootTable.Builder addNetherMetalsAndFlora() {
        LootTable.Builder builder = LootTable.lootTable();
        addNetherMetals(builder);
        addNetherFlora(builder);
        return builder;
    }

    public static LootTable.Builder addNetherMetalsWithExtra() {
        LootTable.Builder builder = LootTable.lootTable();
        builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .setBonusRolls(UniformGenerator.between(0, 1))
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

    public static LootTable.Builder addNetherMetals(LootTable.Builder builder) {
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

    public static LootTable.Builder addNetherFlora(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .setBonusRolls(UniformGenerator.between(0, 1))
                .add(EmptyLootItem.emptyItem()
                        .setWeight(10)
                )
                .add(LootItem.lootTableItem(SgBlocks.NETHERWOOD_SAPLING)
                        .setWeight(20)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                )
                .add(LootItem.lootTableItem(SgItems.NETHER_BANANA)
                        .setWeight(10)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                )
                .add(LootItem.lootTableItem(SgItems.GOLDEN_NETHER_BANANA)
                        .setWeight(1)
                )
        );
        return builder;
    }
}

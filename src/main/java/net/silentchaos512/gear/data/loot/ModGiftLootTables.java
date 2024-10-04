package net.silentchaos512.gear.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgItems;

import java.util.function.BiConsumer;

public class ModGiftLootTables implements LootTableSubProvider {
    public ModGiftLootTables(HolderLookup.Provider provider) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(SgItems.BLUEPRINT_PACKAGE.get().getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.ROD_BLUEPRINT))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GearItemSets.PICKAXE.blueprint()))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GearItemSets.SHOVEL.blueprint()))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GearItemSets.AXE.blueprint()))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GearItemSets.HOE.blueprint()))
                )
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(GearItemSets.SWORD.blueprint()))
                )
        );
    }
}

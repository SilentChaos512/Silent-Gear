package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.silentchaos512.gear.item.BlueprintPackageItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgItems;

public class ModGiftLootTables implements LootTableSubProvider {
    private final LootTableSubProvider.Context output;

    public ModGiftLootTables(LootTableSubProvider.Context output) {
        this.output = output;
    }

    @Override
    public void run() {
        this.output.accept(BlueprintPackageItem.DEFAULT_LOOT_TABLE, LootTable.lootTable()
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

package net.silentchaos512.gear.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaGiftLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.silentchaos512.gear.setup.SgItems;

import java.util.function.BiConsumer;

public class ModGiftLootTables extends VanillaGiftLoot {
    @Override
    public void generate(HolderLookup.Provider p_331027_, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_250831_) {
        p_250831_.accept(SgItems.BLUEPRINT_PACKAGE.get().getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.ROD_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.PICKAXE_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.SHOVEL_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.AXE_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.KNIFE_BLUEPRINT)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.SWORD_BLUEPRINT)
                                .setWeight(11))
                        .add(LootItem.lootTableItem(SgItems.KATANA_BLUEPRINT)
                                .setWeight(5))
                        .add(LootItem.lootTableItem(SgItems.MACHETE_BLUEPRINT)
                                .setWeight(7))
                        .add(LootItem.lootTableItem(SgItems.SPEAR_BLUEPRINT)
                                .setWeight(8)))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.SHIELD_BLUEPRINT))));

        /*for (Item item : SgItems.getItems(item -> item instanceof ICoreItem)) {
            p_accept_1_.accept(SilentGear.getId("random_gear/" + NameUtils.fromItem(item).getPath()), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(item)
                                    .setWeight(3)
                                    .apply(SelectGearTierLootFunction.builder(1)))
                            .add(LootItem.lootTableItem(item)
                                    .setWeight(5)
                                    .apply(SelectGearTierLootFunction.builder(2)))
                            .add(LootItem.lootTableItem(item)
                                    .setWeight(2)
                                    .apply(SelectGearTierLootFunction.builder(3)))));
        }*/

        // FIXME
/*        p_accept_1_.accept(SilentGear.getId("test/ldf_mallet"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(SgItems.HAMMER)
                                .apply(SetPartsFunction.builder(ImmutableList.of(
                                        new LazyPartData(SilentGear.getId("main/diamond")),
                                        new LazyPartData(SilentGear.getId("main/diamond")),
                                        new LazyPartData(SilentGear.getId("main/emerald")),
                                        new LazyPartData(SilentGear.getId("rod/blaze")),
                                        new LazyPartData(SilentGear.getId("tip/redstone")))))
                                .apply(() -> setName(Component.literal("Loliberty Defense Force Mallet")))
                                .apply(() -> setLore(ImmutableList.of(
                                        Component.literal("Standard Issue"),
                                        Component.literal("Protectors of Free Speech")))))));*/
    }
}

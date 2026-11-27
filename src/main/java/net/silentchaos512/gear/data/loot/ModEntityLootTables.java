package net.silentchaos512.gear.data.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.setup.SgLoot;

import java.util.function.BiConsumer;

public class ModEntityLootTables implements LootTableSubProvider {
    private final LootTableSubProvider.Context output;
    private final HolderGetter<Enchantment> enchantments;

    public ModEntityLootTables(LootTableSubProvider.Context output) {
        this.output = output;
        this.enchantments = output.lookup(Registries.ENCHANTMENT);
    }

    @Override
    public void run() {
        this.output.accept(SgLoot.Tables.DROPS_SINEW, addSinew());
        this.output.accept(SgLoot.Tables.DROPS_FINE_SILK_HIGH, addFineSilk(0.04f, 0.01f));
        this.output.accept(SgLoot.Tables.DROPS_FINE_SILK_LOW, addFineSilk(0.02f, 0.005f));
        this.output.accept(SgLoot.Tables.DROPS_LEATHER_SCRAPS_HIGH, addLeatherScraps());
        this.output.accept(SgLoot.Tables.DROPS_LEATHER_SCRAPS_LOW, addLeatherScrapsLow());
    }

    private LootTable.Builder addSinew() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.SINEW)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.enchantments, 0.2f, 0.05f))
                        )
                );
    }

    private LootTable.Builder addFineSilk(float baseChance, float lootingBonus) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.FINE_SILK)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.enchantments, baseChance, lootingBonus))
                        )
                );
    }

    private LootTable.Builder addLeatherScraps() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.LEATHER_SCRAP)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 5)))
                        )
                );
    }

    private LootTable.Builder addLeatherScrapsLow() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.LEATHER_SCRAP)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.enchantments, 0.05f, 0.01f))
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3)))
                        )
                );
    }

    private static void heroOfTheVillage(BiConsumer<Identifier, LootTable.Builder> consumer, Identifier tableName, ItemLike... items) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(1));
        for (ItemLike item : items) {
            pool.add(LootItem.lootTableItem(item));
        }
        consumer.accept(tableName, LootTable.lootTable().withPool(pool));
    }
}

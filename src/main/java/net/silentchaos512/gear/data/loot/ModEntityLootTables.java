package net.silentchaos512.gear.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.setup.SgLoot;

import java.util.function.BiConsumer;

public class ModEntityLootTables extends EntityLootSubProvider {
    protected ModEntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(SgLoot.Tables.DROPS_SINEW, addSinew());
        consumer.accept(SgLoot.Tables.DROPS_FINE_SILK_HIGH, addFineSilk(0.04f, 0.01f));
        consumer.accept(SgLoot.Tables.DROPS_FINE_SILK_LOW, addFineSilk(0.02f, 0.005f));
        consumer.accept(SgLoot.Tables.DROPS_LEATHER_SCRAPS_HIGH, addLeatherScraps());
        consumer.accept(SgLoot.Tables.DROPS_LEATHER_SCRAPS_LOW, addLeatherScrapsLow());

        /*heroOfTheVillage(consumer,
                GearVillages.HOTV_GEAR_SMITH,
                ModItems.ARROW_BLUEPRINT,
                ModItems.EXCAVATOR_BLUEPRINT,
                ModItems.HAMMER_BLUEPRINT,
                ModItems.PAXEL_BLUEPRINT,
                ModItems.PROSPECTOR_HAMMER_BLUEPRINT,
                ModItems.TIP_BLUEPRINT,
                CraftingItems.ADVANCED_UPGRADE_BASE,
                CraftingItems.ROAD_MAKER_UPGRADE,
                CraftingItems.SPOON_UPGRADE
        );*/
    }

    private LootTable.Builder addSinew() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.SINEW)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.2f, 0.05f))
                        )
                );
    }

    private LootTable.Builder addFineSilk(float baseChance, float lootingBonus) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.FINE_SILK)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, baseChance, lootingBonus))
                        )
                );
    }

    private LootTable.Builder addLeatherScraps() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.LEATHER_SCRAP)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                        )
                );
    }

    private LootTable.Builder addLeatherScrapsLow() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.LEATHER_SCRAP)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.05f, 0.01f))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                        )
                );
    }

    private static void heroOfTheVillage(BiConsumer<ResourceLocation, LootTable.Builder> consumer, ResourceLocation tableName, ItemLike... items) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1));
        for (ItemLike item : items) {
            pool.add(LootItem.lootTableItem(item));
        }
        consumer.accept(tableName, LootTable.lootTable().withPool(pool));
    }
}

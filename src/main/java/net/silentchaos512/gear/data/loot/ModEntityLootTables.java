package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.conditions.RandomChanceWithLooting;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.init.GearVillages;
import net.silentchaos512.gear.init.LootInjector;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

import java.util.function.BiConsumer;

public class ModEntityLootTables extends EntityLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(LootInjector.Tables.ENTITIES_CAVE_SPIDER, addFineSilk(0.04f, 0.01f));
        consumer.accept(LootInjector.Tables.ENTITIES_SPIDER, addFineSilk(0.02f, 0.005f));

        heroOfTheVillage(consumer,
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
        );
    }

    private static LootTable.Builder addFineSilk(float baseChance, float lootingBonus) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(CraftingItems.FINE_SILK)
                                .when(KilledByPlayer.killedByPlayer())
                                .when(RandomChanceWithLooting.randomChanceAndLootingBoost(baseChance, lootingBonus))
                        )
                );
    }

    private static void heroOfTheVillage(BiConsumer<ResourceLocation, LootTable.Builder> consumer, ResourceLocation tableName, IItemProvider... items) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantRange.exactly(1));
        for (IItemProvider item : items) {
            pool.add(ItemLootEntry.lootTableItem(item));
        }
        consumer.accept(tableName, LootTable.lootTable().withPool(pool));
    }
}

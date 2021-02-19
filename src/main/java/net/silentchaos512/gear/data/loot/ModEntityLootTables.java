package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.KilledByPlayer;
import net.minecraft.loot.conditions.RandomChanceWithLooting;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.init.LootInjector;
import net.silentchaos512.gear.item.CraftingItems;

import java.util.function.BiConsumer;

public class ModEntityLootTables extends EntityLootTables {
    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(LootInjector.Tables.ENTITIES_CAVE_SPIDER, addFineSilk(0.04f, 0.01f));
        consumer.accept(LootInjector.Tables.ENTITIES_SPIDER, addFineSilk(0.02f, 0.005f));
    }

    private static LootTable.Builder addFineSilk(float baseChance, float lootingBonus) {
        return LootTable.builder()
                .addLootPool(LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(CraftingItems.FINE_SILK)
                                .acceptCondition(KilledByPlayer.builder())
                                .acceptCondition(RandomChanceWithLooting.builder(baseChance, lootingBonus))
                        )
                );
    }
}

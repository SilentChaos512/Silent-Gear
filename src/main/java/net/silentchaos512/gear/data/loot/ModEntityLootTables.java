package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.loot.modifier.LootTableInjectorLootModifier;
import net.silentchaos512.gear.util.ModResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ModEntityLootTables extends EntityLootSubProvider {
    protected ModEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {}

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(getInjectorTableId(EntityType.CAVE_SPIDER.getDefaultLootTable()), addFineSilk(0.04f, 0.01f));
        consumer.accept(getInjectorTableId(EntityType.SPIDER.getDefaultLootTable()), addFineSilk(0.02f, 0.005f));

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

    @NotNull
    private static ModResourceLocation getInjectorTableId(ResourceLocation CAVE_SPIDER) {
        return LootTableInjectorLootModifier.getInjectorTableId(CAVE_SPIDER);
    }

    private static LootTable.Builder addFineSilk(float baseChance, float lootingBonus) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(CraftingItems.FINE_SILK)
                                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(baseChance, lootingBonus))
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

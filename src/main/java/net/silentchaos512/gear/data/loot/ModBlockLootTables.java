package net.silentchaos512.gear.data.loot;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLoot {
    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
    private static final LootItemCondition.Builder SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Tags.Items.SHEARS));
    private static final LootItemCondition.Builder SILK_TOUCH_OR_SHEARS = SHEARS.or(SILK_TOUCH);
    private static final LootItemCondition.Builder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.invert();

    @Override
    protected void addTables() {
        add(ModBlocks.BORT_ORE.get(),
                createOreDrop(ModBlocks.BORT_ORE.get(), CraftingItems.BORT.asItem()));
        add(ModBlocks.DEEPSLATE_BORT_ORE.get(),
                createOreDrop(ModBlocks.DEEPSLATE_BORT_ORE.get(), CraftingItems.BORT.asItem()));
        add(ModBlocks.CRIMSON_IRON_ORE.get(),
                createOreDrop(ModBlocks.CRIMSON_IRON_ORE.get(), CraftingItems.RAW_CRIMSON_IRON.asItem()));
        add(ModBlocks.AZURE_SILVER_ORE.get(),
                createOreDrop(ModBlocks.AZURE_SILVER_ORE.get(), CraftingItems.RAW_AZURE_SILVER.asItem()));
        dropSelf(ModBlocks.RAW_CRIMSON_IRON_BLOCK.get());
        dropSelf(ModBlocks.RAW_AZURE_SILVER_BLOCK.get());
        dropSelf(ModBlocks.BLAZE_GOLD_BLOCK.get());
        dropSelf(ModBlocks.BORT_BLOCK.get());
        dropSelf(ModBlocks.CRIMSON_IRON_BLOCK.get());
        dropSelf(ModBlocks.CRIMSON_STEEL_BLOCK.get());
        dropSelf(ModBlocks.AZURE_SILVER_BLOCK.get());
        dropSelf(ModBlocks.AZURE_ELECTRUM_BLOCK.get());
        dropSelf(ModBlocks.TYRIAN_STEEL_BLOCK.get());
        dropSelf(ModBlocks.MATERIAL_GRADER.get());
        dropSelf(ModBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());
        dropSelf(ModBlocks.NETHERWOOD_FENCE.get());
        dropSelf(ModBlocks.NETHERWOOD_FENCE_GATE.get());
        add(ModBlocks.NETHERWOOD_LEAVES.get(), netherwoodLeaves(ModBlocks.NETHERWOOD_SAPLING, CraftingItems.NETHERWOOD_STICK, DEFAULT_SAPLING_DROP_RATES));
        dropSelf(ModBlocks.NETHERWOOD_LOG.get());
        dropSelf(ModBlocks.STRIPPED_NETHERWOOD_LOG.get());
        dropSelf(ModBlocks.NETHERWOOD_WOOD.get());
        dropSelf(ModBlocks.STRIPPED_NETHERWOOD_WOOD.get());
        dropSelf(ModBlocks.NETHERWOOD_PLANKS.get());
        dropSelf(ModBlocks.NETHERWOOD_SAPLING.get());
        add(ModBlocks.NETHERWOOD_DOOR.get(), block ->
                createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        dropSelf(ModBlocks.NETHERWOOD_TRAPDOOR.get());
        add(ModBlocks.NETHERWOOD_SLAB.get(), BlockLoot::createSlabItemTable);
        dropSelf(ModBlocks.NETHERWOOD_STAIRS.get());
        dropSelf(ModBlocks.STONE_TORCH.get());

        add(ModBlocks.PHANTOM_LIGHT.get(), noDrop());
        dropPottedContents(ModBlocks.POTTED_NETHERWOOD_SAPLING.get());

        dropSelf(ModBlocks.GEAR_SMITHING_TABLE.get());
        dropSelf(ModBlocks.METAL_ALLOYER.get());
        dropSelf(ModBlocks.METAL_PRESS.get());
        dropSelf(ModBlocks.RECRYSTALLIZER.get());
        dropSelf(ModBlocks.REFABRICATOR.get());
        dropSelf(ModBlocks.SALVAGER.get());
        dropSelf(ModBlocks.STARLIGHT_CHARGER.get());

        dropSelf(ModBlocks.WHITE_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.ORANGE_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.MAGENTA_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.LIGHT_BLUE_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.YELLOW_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.LIME_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.PINK_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.GRAY_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.LIGHT_GRAY_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.CYAN_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.PURPLE_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.BLUE_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.BROWN_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.GREEN_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.RED_FLUFFY_BLOCK.get());
        dropSelf(ModBlocks.BLACK_FLUFFY_BLOCK.get());

        this.add(ModBlocks.FLAX_PLANT.get(), flaxPlant(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FLAX_PLANT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(CropBlock.AGE, 7))));
        dropOther(ModBlocks.WILD_FLAX_PLANT.get(), ModItems.FLAX_SEEDS);

        this.add(ModBlocks.FLUFFY_PLANT.get(), fluffyPlant(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FLUFFY_PLANT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(CropBlock.AGE, 7))));
        dropOther(ModBlocks.WILD_FLUFFY_PLANT.get(), ModItems.FLUFFY_SEEDS);
    }

    @Nonnull
    private static Function<Block, LootTable.Builder> netherwoodLeaves(ItemLike sapling, ItemLike stick, float... chances) {
        return (block) -> createSelfDropDispatchTable(block, SILK_TOUCH_OR_SHEARS, applyExplosionCondition(block, LootItem.lootTableItem(sapling))
                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, chances)))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(NOT_SILK_TOUCH_OR_SHEARS)
                        .add(applyExplosionDecay(block, LootItem.lootTableItem(stick)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(NOT_SILK_TOUCH_OR_SHEARS)
                        .add(applyExplosionCondition(block, LootItem.lootTableItem(ModItems.NETHER_BANANA))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    private static LootTable.Builder flaxPlant(LootItemCondition.Builder builder) {
        return applyExplosionDecay(ModBlocks.FLAX_PLANT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLAX_FIBER)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(ModItems.FLAX_SEEDS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLAX_FLOWERS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5f, 1))))
        );
    }

    private static LootTable.Builder fluffyPlant(LootItemCondition.Builder builder) {
        return applyExplosionDecay(ModBlocks.FLUFFY_PLANT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLUFFY_PUFF)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(ModItems.FLUFFY_SEEDS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> SilentGear.MOD_ID.equals(block.getRegistryName().getNamespace()))
                .collect(Collectors.toSet());
    }
}

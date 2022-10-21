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
import net.silentchaos512.gear.init.SgBlocks;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.util.NameUtils;

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
        add(SgBlocks.BORT_ORE.get(),
                createOreDrop(SgBlocks.BORT_ORE.get(), CraftingItems.BORT.asItem()));
        add(SgBlocks.DEEPSLATE_BORT_ORE.get(),
                createOreDrop(SgBlocks.DEEPSLATE_BORT_ORE.get(), CraftingItems.BORT.asItem()));
        add(SgBlocks.CRIMSON_IRON_ORE.get(),
                createOreDrop(SgBlocks.CRIMSON_IRON_ORE.get(), CraftingItems.RAW_CRIMSON_IRON.asItem()));
        add(SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE.get(),
                createOreDrop(SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE.get(), CraftingItems.RAW_CRIMSON_IRON.asItem()));
        add(SgBlocks.AZURE_SILVER_ORE.get(),
                createOreDrop(SgBlocks.AZURE_SILVER_ORE.get(), CraftingItems.RAW_AZURE_SILVER.asItem()));
        dropSelf(SgBlocks.RAW_CRIMSON_IRON_BLOCK.get());
        dropSelf(SgBlocks.RAW_AZURE_SILVER_BLOCK.get());
        dropSelf(SgBlocks.BLAZE_GOLD_BLOCK.get());
        dropSelf(SgBlocks.BORT_BLOCK.get());
        dropSelf(SgBlocks.CRIMSON_IRON_BLOCK.get());
        dropSelf(SgBlocks.CRIMSON_STEEL_BLOCK.get());
        dropSelf(SgBlocks.AZURE_SILVER_BLOCK.get());
        dropSelf(SgBlocks.AZURE_ELECTRUM_BLOCK.get());
        dropSelf(SgBlocks.TYRIAN_STEEL_BLOCK.get());
        dropSelf(SgBlocks.MATERIAL_GRADER.get());
        dropSelf(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());
        dropSelf(SgBlocks.NETHERWOOD_FENCE.get());
        dropSelf(SgBlocks.NETHERWOOD_FENCE_GATE.get());
        add(SgBlocks.NETHERWOOD_LEAVES.get(), netherwoodLeaves(SgBlocks.NETHERWOOD_SAPLING, CraftingItems.NETHERWOOD_STICK, DEFAULT_SAPLING_DROP_RATES));
        dropSelf(SgBlocks.NETHERWOOD_LOG.get());
        dropSelf(SgBlocks.STRIPPED_NETHERWOOD_LOG.get());
        dropSelf(SgBlocks.NETHERWOOD_WOOD.get());
        dropSelf(SgBlocks.STRIPPED_NETHERWOOD_WOOD.get());
        dropSelf(SgBlocks.NETHERWOOD_PLANKS.get());
        dropSelf(SgBlocks.NETHERWOOD_SAPLING.get());
        add(SgBlocks.NETHERWOOD_DOOR.get(), block ->
                createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        dropSelf(SgBlocks.NETHERWOOD_TRAPDOOR.get());
        add(SgBlocks.NETHERWOOD_SLAB.get(), BlockLoot::createSlabItemTable);
        dropSelf(SgBlocks.NETHERWOOD_STAIRS.get());
        dropSelf(SgBlocks.STONE_TORCH.get());

        add(SgBlocks.PHANTOM_LIGHT.get(), noDrop());
        dropPottedContents(SgBlocks.POTTED_NETHERWOOD_SAPLING.get());

        dropSelf(SgBlocks.GEAR_SMITHING_TABLE.get());
        dropSelf(SgBlocks.METAL_ALLOYER.get());
        dropSelf(SgBlocks.METAL_PRESS.get());
        dropSelf(SgBlocks.RECRYSTALLIZER.get());
        dropSelf(SgBlocks.REFABRICATOR.get());
        dropSelf(SgBlocks.SALVAGER.get());
        dropSelf(SgBlocks.STARLIGHT_CHARGER.get());

        dropSelf(SgBlocks.WHITE_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.ORANGE_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.MAGENTA_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.LIGHT_BLUE_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.YELLOW_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.LIME_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.PINK_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.GRAY_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.LIGHT_GRAY_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.CYAN_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.PURPLE_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.BLUE_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.BROWN_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.GREEN_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.RED_FLUFFY_BLOCK.get());
        dropSelf(SgBlocks.BLACK_FLUFFY_BLOCK.get());

        this.add(SgBlocks.FLAX_PLANT.get(), flaxPlant(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SgBlocks.FLAX_PLANT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(CropBlock.AGE, 7))));
        dropOther(SgBlocks.WILD_FLAX_PLANT.get(), SgItems.FLAX_SEEDS);

        this.add(SgBlocks.FLUFFY_PLANT.get(), fluffyPlant(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SgBlocks.FLUFFY_PLANT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(CropBlock.AGE, 7))));
        dropOther(SgBlocks.WILD_FLUFFY_PLANT.get(), SgItems.FLUFFY_SEEDS);
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
                        .add(applyExplosionCondition(block, LootItem.lootTableItem(SgItems.NETHER_BANANA))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    private static LootTable.Builder flaxPlant(LootItemCondition.Builder builder) {
        return applyExplosionDecay(SgBlocks.FLAX_PLANT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLAX_FIBER)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(SgItems.FLAX_SEEDS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLAX_FLOWERS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5f, 1))))
        );
    }

    private static LootTable.Builder fluffyPlant(LootItemCondition.Builder builder) {
        return applyExplosionDecay(SgBlocks.FLUFFY_PLANT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLUFFY_PUFF)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(SgItems.FLUFFY_SEEDS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> SilentGear.MOD_ID.equals(NameUtils.fromBlock(block).getNamespace()))
                .collect(Collectors.toSet());
    }
}

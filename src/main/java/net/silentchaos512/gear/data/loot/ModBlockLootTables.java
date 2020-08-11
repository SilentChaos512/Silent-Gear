package net.silentchaos512.gear.data.loot;

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.MatchTool;
import net.minecraft.world.storage.loot.conditions.TableBonus;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {
    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
    private static final ILootCondition.IBuilder SHEARS = MatchTool.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
    private static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.alternative(SILK_TOUCH);
    private static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.inverted();

    @Override
    protected void addTables() {
        registerDropSelfLootTable(ModBlocks.CRIMSON_IRON_ORE.get());
        registerDropSelfLootTable(ModBlocks.AZURE_SILVER_ORE.get());
        registerDropSelfLootTable(ModBlocks.BLAZE_GOLD_BLOCK.get());
        registerDropSelfLootTable(ModBlocks.CRAFTING_STATION.get());
        registerDropSelfLootTable(ModBlocks.CRIMSON_IRON_BLOCK.get());
        registerDropSelfLootTable(ModBlocks.CRIMSON_STEEL_BLOCK.get());
        registerDropSelfLootTable(ModBlocks.AZURE_SILVER_BLOCK.get());
        registerDropSelfLootTable(ModBlocks.AZURE_ELECTRUM_BLOCK.get());
        ILootCondition.IBuilder flaxBuilder = BlockStateProperty.builder(ModBlocks.FLAX_PLANT.get()).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(CropsBlock.AGE, 7));
        this.registerLootTable(ModBlocks.FLAX_PLANT.get(), flax(ModItems.FLAXSEEDS, flaxBuilder));
        registerDropSelfLootTable(ModBlocks.MATERIAL_GRADER.get());
        registerDropSelfLootTable(ModBlocks.NETHERWOOD_FENCE.get());
        registerLootTable(ModBlocks.NETHERWOOD_LEAVES.get(), netherwoodLeaves(ModBlocks.NETHERWOOD_SAPLING, CraftingItems.NETHERWOOD_STICK, DEFAULT_SAPLING_DROP_RATES));
        registerDropSelfLootTable(ModBlocks.NETHERWOOD_LOG.get());
        registerDropSelfLootTable(ModBlocks.NETHERWOOD_PLANKS.get());
        registerDropSelfLootTable(ModBlocks.NETHERWOOD_SAPLING.get());
        registerLootTable(ModBlocks.NETHERWOOD_DOOR.get(), block ->
                droppingWhen(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        registerDropSelfLootTable(ModBlocks.NETHERWOOD_TRAPDOOR.get());
        registerLootTable(ModBlocks.NETHERWOOD_SLAB.get(), BlockLootTables::droppingSlab);
        registerDropSelfLootTable(ModBlocks.NETHERWOOD_STAIRS.get());
        registerLootTable(ModBlocks.PHANTOM_LIGHT.get(), func_218482_a());
        registerFlowerPot(ModBlocks.POTTED_NETHERWOOD_SAPLING.get());
        registerDropSelfLootTable(ModBlocks.SALVAGER.get());
        registerDropSelfLootTable(ModBlocks.STONE_TORCH.get());
        registerDropSelfLootTable(ModBlocks.STRIPPED_NETHERWOOD_LOG.get());
        registerDropping(ModBlocks.WILD_FLAX_PLANT.get(), ModItems.FLAXSEEDS);
    }

    @Nonnull
    private static Function<Block, LootTable.Builder> netherwoodLeaves(IItemProvider sapling, IItemProvider stick, float... chances) {
        return (block) -> droppingWithSilkTouchOrShears(block, withSurvivesExplosion(block, ItemLootEntry.builder(sapling))
                .acceptCondition(TableBonus.builder(Enchantments.FORTUNE, chances)))
                .addLootPool(LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .acceptCondition(NOT_SILK_TOUCH_OR_SHEARS)
                        .addEntry(withExplosionDecay(block, ItemLootEntry.builder(stick)
                                .acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 2.0F))))
                                .acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))))
                .addLootPool(LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .acceptCondition(NOT_SILK_TOUCH_OR_SHEARS)
                        .addEntry(withSurvivesExplosion(block, ItemLootEntry.builder(ModItems.NETHER_BANANA))
                                .acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    private static LootTable.Builder flax(IItemProvider seedItem, ILootCondition.IBuilder builder) {
        return withExplosionDecay(ModBlocks.FLAX_PLANT, LootTable.builder()
                .addLootPool(LootPool.builder()
                        .acceptCondition(builder)
                        .addEntry(ItemLootEntry.builder(CraftingItems.FLAX_FIBER)
                                .acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3))))
                .addLootPool(LootPool.builder()
                        .acceptCondition(builder)
                        .addEntry(ItemLootEntry.builder(seedItem)
                                .acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> SilentGear.MOD_ID.equals(block.getRegistryName().getNamespace()))
                .collect(Collectors.toSet());
    }
}

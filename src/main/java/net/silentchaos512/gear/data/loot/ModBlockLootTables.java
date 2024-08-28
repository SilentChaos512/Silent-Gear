package net.silentchaos512.gear.data.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collections;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootSubProvider {
    protected ModBlockLootTables(HolderLookup.Provider provider) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
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
        add(SgBlocks.NETHERWOOD_LEAVES.get(), block ->
                createNetherwoodLeaves(block, SgBlocks.NETHERWOOD_SAPLING.get(), 0.05F, 0.0625F, 0.083333336F, 0.1F)
        );
        dropSelf(SgBlocks.NETHERWOOD_LOG.get());
        dropSelf(SgBlocks.STRIPPED_NETHERWOOD_LOG.get());
        dropSelf(SgBlocks.NETHERWOOD_WOOD.get());
        dropSelf(SgBlocks.STRIPPED_NETHERWOOD_WOOD.get());
        dropSelf(SgBlocks.NETHERWOOD_PLANKS.get());
        dropSelf(SgBlocks.NETHERWOOD_SAPLING.get());
        add(SgBlocks.NETHERWOOD_DOOR.get(), block ->
                createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        dropSelf(SgBlocks.NETHERWOOD_TRAPDOOR.get());
        add(SgBlocks.NETHERWOOD_SLAB.get(), this::createSlabItemTable);
        dropSelf(SgBlocks.NETHERWOOD_STAIRS.get());
        dropSelf(SgBlocks.STONE_TORCH.get());

        add(SgBlocks.PHANTOM_LIGHT.get(), noDrop());
        dropPottedContents(SgBlocks.POTTED_NETHERWOOD_SAPLING.get());

        dropSelf(SgBlocks.GEAR_SMITHING_TABLE.get());
        dropSelf(SgBlocks.ALLOY_FORGE.get());
        dropSelf(SgBlocks.METAL_PRESS.get());
        dropSelf(SgBlocks.RECRYSTALLIZER.get());
        dropSelf(SgBlocks.REFABRICATOR.get());
        dropSelf(SgBlocks.SALVAGER.get());
        dropSelf(SgBlocks.STARLIGHT_CHARGER.get());
        dropSelf(SgBlocks.STONE_ANVIL.get());

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

    private LootTable.Builder createNetherwoodLeaves(Block leavesBlock, Block saplingBlock, float... chances) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchOrShearsDispatchTable(
                        leavesBlock,
                        ((LootPoolSingletonContainer.Builder<?>) this.applyExplosionCondition(leavesBlock, LootItem.lootTableItem(saplingBlock)))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), chances))
                )
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(
                                        ((LootPoolSingletonContainer.Builder<?>) this.applyExplosionDecay(
                                                leavesBlock, LootItem.lootTableItem(CraftingItems.NETHERWOOD_STICK)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                        ))
                                                .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
                                )
                )
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(
                                        ((LootPoolSingletonContainer.Builder<?>) this.applyExplosionCondition(leavesBlock, LootItem.lootTableItem(SgItems.NETHER_BANANA)))
                                                .when(
                                                        BonusLevelTableCondition.bonusLevelFlatChance(
                                                                registrylookup.getOrThrow(Enchantments.FORTUNE), 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F
                                                        )
                                                )
                                )
                );
    }

    private LootTable.Builder flaxPlant(LootItemCondition.Builder builder) {
        var registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        var fortune = registrylookup.getOrThrow(Enchantments.FORTUNE);

        return applyExplosionDecay(SgBlocks.FLAX_PLANT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLAX_FIBER)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(SgItems.FLAX_SEEDS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLAX_FLOWERS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.5f, 1))))
        );
    }

    private LootTable.Builder fluffyPlant(LootItemCondition.Builder builder) {
        var registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        var fortune = registrylookup.getOrThrow(Enchantments.FORTUNE);

        return applyExplosionDecay(SgBlocks.FLUFFY_PLANT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(CraftingItems.FLUFFY_PUFF)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.5714286F, 3))))
                .withPool(LootPool.lootPool()
                        .when(builder)
                        .add(LootItem.lootTableItem(SgItems.FLUFFY_SEEDS)
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.5714286F, 3))))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                .filter(block -> SilentGear.MOD_ID.equals(NameUtils.fromBlock(block).getNamespace()))
                .collect(Collectors.toSet());
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return HAS_SHEARS.or(this.hasSilkTouch());
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return this.hasShearsOrSilkTouch().invert();
    }
}

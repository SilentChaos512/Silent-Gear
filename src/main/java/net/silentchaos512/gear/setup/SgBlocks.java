package net.silentchaos512.gear.setup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.*;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlock;
import net.silentchaos512.gear.block.charger.ChargerBlockEntity;
import net.silentchaos512.gear.block.charger.StarlightChargerBlock;
import net.silentchaos512.gear.block.grader.GraderBlock;
import net.silentchaos512.gear.block.press.MetalPressBlock;
import net.silentchaos512.gear.block.salvager.SalvagerBlock;
import net.silentchaos512.gear.block.stoneanvil.StoneAnvilBlock;
import net.silentchaos512.gear.crafting.recipe.alloy.FabricAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.GemAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.MetalAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.SuperAlloyRecipe;
import net.silentchaos512.gear.item.block.AlloyMakerBlockItem;
import net.silentchaos512.gear.item.block.BlockItemWithTooltip;
import net.silentchaos512.gear.item.block.OreBlockItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = SilentGear.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class SgBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SilentGear.MOD_ID);

    private static final Map<Block, Block> STRIPPED_WOOD = new HashMap<>();

    public static final DeferredBlock<DropExperienceBlock> BORT_ORE = register(
            "bort_ore",
            properties -> getOre(UniformInt.of(3, 7), SoundType.STONE, properties),
            SgBlocks::oreBlockItem
    );
    public static final DeferredBlock<DropExperienceBlock> DEEPSLATE_BORT_ORE = register(
            "deepslate_bort_ore",
            properties -> getOre(UniformInt.of(3, 7), SoundType.DEEPSLATE, properties),
            SgBlocks::oreBlockItem
    );
    public static final DeferredBlock<DropExperienceBlock> CRIMSON_IRON_ORE = register(
            "crimson_iron_ore",
            properties -> getOre(ConstantInt.of(0), SoundType.NETHER_GOLD_ORE, properties),
            SgBlocks::oreBlockItem
    );
    public static final DeferredBlock<DropExperienceBlock> BLACKSTONE_CRIMSON_IRON_ORE = register(
            "blackstone_crimson_iron_ore",
            properties -> getOre(ConstantInt.of(0), SoundType.GILDED_BLACKSTONE, properties),
            SgBlocks::oreBlockItem
    );
    public static final DeferredBlock<DropExperienceBlock> AZURE_SILVER_ORE = register(
            "azure_silver_ore",
            properties -> getOre(ConstantInt.of(0), SoundType.STONE, properties),
            SgBlocks::oreBlockItem
    );

    public static final DeferredBlock<Block> RAW_CRIMSON_IRON_BLOCK = register(
            "raw_crimson_iron_block",
            properties -> getRawOreBlock(SoundType.NETHER_GOLD_ORE, properties),
            SgBlocks::oreBlockItem
    );
    public static final DeferredBlock<Block> RAW_AZURE_SILVER_BLOCK = register(
            "raw_azure_silver_block",
            properties -> getRawOreBlock(SoundType.STONE, properties),
            SgBlocks::oreBlockItem
    );

    public static final DeferredBlock<Block> BORT_BLOCK = register("bort_block",
            SgBlocks::getStorageBlock);
    public static final DeferredBlock<Block> CRIMSON_IRON_BLOCK = register("crimson_iron_block",
            SgBlocks::getStorageBlock);
    public static final DeferredBlock<Block> CRIMSON_STEEL_BLOCK = register("crimson_steel_block",
            SgBlocks::getStorageBlock);
    public static final DeferredBlock<Block> BLAZE_GOLD_BLOCK = register("blaze_gold_block",
            SgBlocks::getStorageBlock);
    public static final DeferredBlock<Block> AZURE_SILVER_BLOCK = register("azure_silver_block",
            SgBlocks::getStorageBlock);
    public static final DeferredBlock<Block> AZURE_ELECTRUM_BLOCK = register("azure_electrum_block",
            SgBlocks::getStorageBlock);
    public static final DeferredBlock<Block> TYRIAN_STEEL_BLOCK = register("tyrian_steel_block",
            SgBlocks::getStorageBlock);

    public static final DeferredBlock<Block> GEAR_SMITHING_TABLE = register(
            "gear_smithing_table",
            GearSmithingTableBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .sound(SoundType.WOOD)
    );

    public static final DeferredBlock<StoneAnvilBlock> STONE_ANVIL = register(
            "stone_anvil",
            StoneAnvilBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(3, 10),
            SgBlocks::blockItemWithTooltip
    );

    public static final DeferredBlock<GraderBlock> MATERIAL_GRADER = register(
            "material_grader",
            GraderBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(5, 30),
            SgBlocks::blockItemWithTooltip
    );

    public static final DeferredBlock<SalvagerBlock> SALVAGER = register(
            "salvager",
            SalvagerBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(5, 30),
            SgBlocks::blockItemWithTooltip
    );

    public static final DeferredBlock<StarlightChargerBlock> STARLIGHT_CHARGER = register(
            "starlight_charger",
            properties -> new StarlightChargerBlock(ChargerBlockEntity::createStarlightCharger, properties),
            BlockBehaviour.Properties.of()
                    .strength(5, 30),
            SgBlocks::blockItemWithTooltip
    );

    public static final DeferredBlock<AlloyMakerBlock<MetalAlloyRecipe>> ALLOY_FORGE = register(
            "alloy_forge",
            properties -> new AlloyMakerBlock<>(Const.METAL_ALLOY_MAKER_INFO, properties),
            BlockBehaviour.Properties.of()
                    .strength(4, 20)
                    .sound(SoundType.METAL),
            deferredBlock -> itemProperties -> new AlloyMakerBlockItem(deferredBlock.get(), itemProperties)
    );

    public static final DeferredBlock<AlloyMakerBlock<GemAlloyRecipe>> RECRYSTALLIZER = register(
            "recrystallizer",
            properties -> new AlloyMakerBlock<>(Const.GEM_ALLOY_MAKER_INFO, properties),
            BlockBehaviour.Properties.of()
                    .strength(4, 20)
                    .sound(SoundType.METAL),
            deferredBlock -> itemProperties -> new AlloyMakerBlockItem(deferredBlock.get(), itemProperties)
    );

    public static final DeferredBlock<AlloyMakerBlock<FabricAlloyRecipe>> REFABRICATOR = register(
            "refabricator",
            properties -> new AlloyMakerBlock<>(Const.FABRIC_ALLOY_MAKER_INFO, properties),
            BlockBehaviour.Properties.of()
                    .strength(4, 20)
                    .sound(SoundType.METAL),
            deferredBlock -> itemProperties -> new AlloyMakerBlockItem(deferredBlock.get(), itemProperties)
    );

    public static final DeferredBlock<AlloyMakerBlock<SuperAlloyRecipe>> SUPER_MIXER = register(
            "super_mixer",
            properties -> new AlloyMakerBlock<>(Const.SUPER_MIXER_INFO, properties) {
                @Override
                public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
                    return Shapes.block();
                }
            },
            BlockBehaviour.Properties.of()
                    .strength(4, 20)
                    .sound(SoundType.METAL),
            deferredBlock -> itemProperties -> new AlloyMakerBlockItem(deferredBlock.get(), itemProperties)
    );

    public static final DeferredBlock<MetalPressBlock> METAL_PRESS = register(
            "metal_press",
            MetalPressBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(4, 20)
                    .sound(SoundType.METAL),
            SgBlocks::blockItemWithTooltip
    );

    public static final DeferredBlock<ModCropBlock> FLAX_PLANT = registerNoItem(
            "flax_plant",
            properties -> new ModCropBlock(SgItems.FLAX_SEEDS::get, properties),
            BlockBehaviour.Properties.of()
                    .strength(0)
                    .noCollission()
                    .randomTicks()
                    .sound(SoundType.CROP)
    );
    public static final DeferredBlock<BushBlock> WILD_FLAX_PLANT = registerNoItem(
            "wild_flax_plant",
            BushBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0)
                    .noCollission()
                    .sound(SoundType.CROP)
    );
    public static final DeferredBlock<ModCropBlock> FLUFFY_PLANT = registerNoItem(
            "fluffy_plant",
            properties -> new ModCropBlock(SgItems.FLUFFY_SEEDS::get, properties),
            BlockBehaviour.Properties.of()
                    .strength(0)
                    .noCollission()
                    .randomTicks()
                    .sound(SoundType.CROP)
    );
    public static final DeferredBlock<BushBlock> WILD_FLUFFY_PLANT = registerNoItem(
            "wild_fluffy_plant",
            BushBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0)
                    .noCollission()
                    .sound(SoundType.CROP)
    );

    public static final DeferredBlock<FluffyBlock> WHITE_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.WHITE);
    public static final DeferredBlock<FluffyBlock> ORANGE_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.ORANGE);
    public static final DeferredBlock<FluffyBlock> MAGENTA_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.MAGENTA);
    public static final DeferredBlock<FluffyBlock> LIGHT_BLUE_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.LIGHT_BLUE);
    public static final DeferredBlock<FluffyBlock> YELLOW_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.YELLOW);
    public static final DeferredBlock<FluffyBlock> LIME_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.LIME);
    public static final DeferredBlock<FluffyBlock> PINK_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.PINK);
    public static final DeferredBlock<FluffyBlock> GRAY_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.GRAY);
    public static final DeferredBlock<FluffyBlock> LIGHT_GRAY_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.LIGHT_GRAY);
    public static final DeferredBlock<FluffyBlock> CYAN_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.CYAN);
    public static final DeferredBlock<FluffyBlock> PURPLE_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.PURPLE);
    public static final DeferredBlock<FluffyBlock> BLUE_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.BLUE);
    public static final DeferredBlock<FluffyBlock> BROWN_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.BROWN);
    public static final DeferredBlock<FluffyBlock> GREEN_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.GREEN);
    public static final DeferredBlock<FluffyBlock> RED_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.RED);
    public static final DeferredBlock<FluffyBlock> BLACK_FLUFFY_BLOCK = registerFluffyBlock(DyeColor.BLACK);

    public static final DeferredBlock<TorchBlock> STONE_TORCH = register(
            "stone_torch",
            properties -> new TorchBlock(ParticleTypes.FLAME, properties),
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0)
                    .lightLevel(state -> 14)
                    .sound(SoundType.STONE),
            deferredBlock -> getStoneTorchItem()
    );
    public static final DeferredBlock<WallTorchBlock> WALL_STONE_TORCH = registerNoItem(
            "wall_stone_torch",
            properties -> new WallTorchBlock(ParticleTypes.FLAME, properties),
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0)
                    .lightLevel(state -> 14)
                    .sound(SoundType.STONE)
    );

    public static final DeferredBlock<Block> NETHERWOOD_CHARCOAL_BLOCK = register(
            "netherwood_charcoal_block",
            Block::new,
            BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5, 6)
    );

    public static final DeferredBlock<WoodBlock> NETHERWOOD_LOG = register(
            "netherwood_log",
            properties -> new WoodBlock(STRIPPED_WOOD::get, properties),
            netherWoodProps(2f, 2f)
    );
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_NETHERWOOD_LOG = register(
            "stripped_netherwood_log",
            RotatedPillarBlock::new,
            netherWoodProps(2f, 2f)
    );
    public static final DeferredBlock<WoodBlock> NETHERWOOD_WOOD = register(
            "netherwood_wood",
            properties -> new WoodBlock(STRIPPED_WOOD::get, properties),
            netherWoodProps(2f, 2f)
    );
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_NETHERWOOD_WOOD = register(
            "stripped_netherwood_wood",
            RotatedPillarBlock::new,
            netherWoodProps(2f, 2f)
    );

    public static final DeferredBlock<Block> NETHERWOOD_PLANKS = register(
            "netherwood_planks",
            Block::new,
            netherWoodProps(2f, 3f)
    );
    public static final DeferredBlock<SlabBlock> NETHERWOOD_SLAB = register(
            "netherwood_slab",
            SlabBlock::new,
            netherWoodProps(2f, 3f)
    );
    public static final DeferredBlock<StairBlock> NETHERWOOD_STAIRS = register(
            "netherwood_stairs",
            properties -> new StairBlock(NETHERWOOD_PLANKS.get().defaultBlockState(), properties),
            netherWoodProps(2f, 3f)
    );
    public static final DeferredBlock<FenceBlock> NETHERWOOD_FENCE = register(
            "netherwood_fence",
            FenceBlock::new,
            netherWoodProps(2f, 3f)
    );
    public static final DeferredBlock<FenceGateBlock> NETHERWOOD_FENCE_GATE = register(
            "netherwood_fence_gate",
            properties -> new FenceGateBlock(properties, SoundEvents.NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.NETHER_WOOD_FENCE_GATE_OPEN),
            netherWoodProps(2f, 3f)
    );
    public static final DeferredBlock<DoorBlock> NETHERWOOD_DOOR = register(
            "netherwood_door",
            properties -> new DoorBlock(BlockSetType.CRIMSON, properties),
            netherWoodProps(3f, 3f)
                    .noOcclusion()
    );
    public static final DeferredBlock<TrapDoorBlock> NETHERWOOD_TRAPDOOR = register(
            "netherwood_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.CRIMSON, properties),
            netherWoodProps(3f, 3f)
                    .noOcclusion()
    );
    public static final DeferredBlock<LeavesBlock> NETHERWOOD_LEAVES = register(
            "netherwood_leaves",
            properties -> new UntintedParticleLeavesBlock(
                    0.01f,
                    ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, 0x8F005F),
                    properties
            ),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn(Blocks::ocelotOrParrot)
                    .isSuffocating(SgBlocks::never)
                    .isViewBlocking(SgBlocks::never)
                    .pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor(SgBlocks::never)
    );
    public static final DeferredBlock<NetherwoodSapling> NETHERWOOD_SAPLING = register(
            "netherwood_sapling",
            NetherwoodSapling::new,
            BlockBehaviour.Properties.of()
                    .strength(0)
                    .noCollission()
                    .randomTicks()
                    .sound(SoundType.GRASS)
    );

    public static final DeferredBlock<FlowerPotBlock> POTTED_NETHERWOOD_SAPLING = registerNoItem(
            "potted_netherwood_sapling",
            properties -> makePottedPlant(NETHERWOOD_SAPLING, properties),
            Block.Properties.of()
                    .strength(0)
    );
    public static final DeferredBlock<PhantomLight> PHANTOM_LIGHT = register(
            "phantom_light",
            PhantomLight::new,
            BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5f, 6000000.0f)
                    .lightLevel(state -> 15)
    );

    private SgBlocks() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        STRIPPED_WOOD.put(NETHERWOOD_LOG.get(), STRIPPED_NETHERWOOD_LOG.get());
        STRIPPED_WOOD.put(NETHERWOOD_WOOD.get(), STRIPPED_NETHERWOOD_WOOD.get());
    }

    private static DropExperienceBlock getOre(IntProvider xpDrop, SoundType soundType, BlockBehaviour.Properties properties) {
        return new DropExperienceBlock(xpDrop, properties
                .strength(4, 10)
                .requiresCorrectToolForDrops()
                .sound(soundType));
    }

    private static Block getRawOreBlock(SoundType soundType, BlockBehaviour.Properties properties) {
        return new DropExperienceBlock(ConstantInt.of(0), properties
                .strength(4, 20)
                .requiresCorrectToolForDrops()
                .sound(soundType));
    }

    private static Block getStorageBlock(BlockBehaviour.Properties properties) {
        return new Block(properties
                .strength(3, 6)
                .sound(SoundType.METAL));
    }

    private static <T extends Block> DeferredBlock<T> registerNoItem(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            BlockBehaviour.Properties properties
    ) {
        return BLOCKS.registerBlock(name, block, properties);
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block
    ) {
        return register(name, block, BlockBehaviour.Properties.of(), SgBlocks::defaultItem, new Item.Properties().useBlockDescriptionPrefix());
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            BlockBehaviour.Properties properties
    ) {
        return register(name, block, properties, SgBlocks::defaultItem, new Item.Properties().useBlockDescriptionPrefix());
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            BlockBehaviour.Properties properties,
            Function<DeferredBlock<T>, Function<Item.Properties, ? extends BlockItem>> item
    ) {
        return register(name, block, properties, item, new Item.Properties().useBlockDescriptionPrefix());
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            Function<DeferredBlock<T>, Function<Item.Properties, ? extends BlockItem>> item
    ) {
        return register(name, block, BlockBehaviour.Properties.of(), item, new Item.Properties().useBlockDescriptionPrefix());
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            Function<DeferredBlock<T>, Function<Item.Properties, ? extends BlockItem>> item,
            Item.Properties itemProperties
    ) {
        return register(name, block, BlockBehaviour.Properties.of(), item, itemProperties);
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name,
            Function<BlockBehaviour.Properties, T> block,
            BlockBehaviour.Properties properties,
            Function<DeferredBlock<T>, Function<Item.Properties, ? extends BlockItem>> item,
            Item.Properties itemProperties
    ) {
        DeferredBlock<T> ret = registerNoItem(name, block, properties);
        SgItems.register(name, item.apply(ret), itemProperties);
        return ret;
    }

    private static DeferredBlock<FluffyBlock> registerFluffyBlock(DyeColor color) {
        return register(
                color.getName() + "_fluffy_block",
                props -> new FluffyBlock(color, props),
                BlockBehaviour.Properties.of()
                        .strength(0.8f, 3)
                        .sound(SoundType.WOOL)
        );
    }

    private static <T extends Block> Function<Item.Properties, BlockItem> defaultItem(DeferredBlock<T> block) {
        return p -> new BlockItem(block.get(), p);
    }

    private static Function<Item.Properties, BlockItem> getStoneTorchItem() {
        return itemProperties -> new StandingAndWallBlockItem(STONE_TORCH.get(), WALL_STONE_TORCH.get(), Direction.DOWN, itemProperties);
    }

    @SuppressWarnings("SameParameterValue")
    private static FlowerPotBlock makePottedPlant(Supplier<? extends Block> flower, BlockBehaviour.Properties properties) {
        FlowerPotBlock potted = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, flower, properties);
        ResourceLocation flowerId = NameUtils.fromBlock(flower.get());
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(flowerId, () -> potted);
        return potted;
    }

    private static BlockBehaviour.Properties netherWoodProps(float hardnessIn, float resistanceIn) {
        return BlockBehaviour.Properties.of()
                .strength(hardnessIn, resistanceIn)
                .sound(SoundType.WOOD);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> Collection<T> getBlocks(Class<T> clazz) {
        return BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(clazz::isInstance)
                .map(block -> (T) block)
                .collect(Collectors.toList());
    }

    private static Function<Item.Properties, BlockItem> blockItemWithTooltip(DeferredBlock<?> block) {
        return properties -> new BlockItemWithTooltip(
                block.get(),
                properties.useItemDescriptionPrefix()
        );
    }

    private static Function<Item.Properties, BlockItem> oreBlockItem(DeferredBlock<?> block) {
        return properties -> new OreBlockItem(
                block.get(),
                properties.useItemDescriptionPrefix()
        );
    }

    private static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return Blocks.never(state, level, pos, null);
    }
}

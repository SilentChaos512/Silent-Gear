package net.silentchaos512.gear.init;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.block.NetherwoodSapling;
import net.silentchaos512.gear.block.PhantomLight;
import net.silentchaos512.gear.block.compounder.CompounderBlock;
import net.silentchaos512.gear.block.WoodBlock;
import net.silentchaos512.gear.block.compounder.CompounderBlock;
import net.silentchaos512.gear.block.grader.GraderBlock;
import net.silentchaos512.gear.block.salvager.SalvagerBlock;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.registry.BlockRegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBlocks {
    private static final Map<Block, Block> STRIPPED_WOOD = new HashMap<>();

    public static final BlockRegistryObject<OreBlock> BORT_ORE = register("bort_ore", () ->
            getOre(2, SoundType.STONE));
    public static final BlockRegistryObject<OreBlock> CRIMSON_IRON_ORE = register("crimson_iron_ore", () ->
            getOre(2, SoundType.NETHER_GOLD));
    public static final BlockRegistryObject<OreBlock> AZURE_SILVER_ORE = register("azure_silver_ore", () ->
            getOre(4, SoundType.STONE));

    public static final BlockRegistryObject<Block> BORT_BLOCK = register("bort_block", ModBlocks::getStorageBlock);
    public static final BlockRegistryObject<Block> CRIMSON_IRON_BLOCK = register("crimson_iron_block", ModBlocks::getStorageBlock);
    public static final BlockRegistryObject<Block> CRIMSON_STEEL_BLOCK = register("crimson_steel_block", ModBlocks::getStorageBlock);
    public static final BlockRegistryObject<Block> BLAZE_GOLD_BLOCK = register("blaze_gold_block", ModBlocks::getStorageBlock);
    public static final BlockRegistryObject<Block> AZURE_SILVER_BLOCK = register("azure_silver_block", ModBlocks::getStorageBlock);
    public static final BlockRegistryObject<Block> AZURE_ELECTRUM_BLOCK = register("azure_electrum_block", ModBlocks::getStorageBlock);

    public static final BlockRegistryObject<GraderBlock> MATERIAL_GRADER = register("material_grader", () ->
            new GraderBlock(AbstractBlock.Properties.create(Material.IRON)
                    .hardnessAndResistance(5, 30)));
    public static final BlockRegistryObject<SalvagerBlock> SALVAGER = register("salvager", () ->
            new SalvagerBlock(AbstractBlock.Properties.create(Material.IRON)
                    .hardnessAndResistance(4, 20)));

    public static final BlockRegistryObject<CompounderBlock> METAL_ALLOYER = register("metal_alloyer", () ->
            new CompounderBlock(Const.METAL_COMPOUNDER_INFO,
                    AbstractBlock.Properties.create(Material.IRON)
                            .hardnessAndResistance(4, 20)
                            .sound(SoundType.METAL)));
    public static final BlockRegistryObject<CompounderBlock> RECRYSTALLIZER = register("recrystallizer", () ->
            new CompounderBlock(Const.GEM_COMPOUNDER_INFO,
                    AbstractBlock.Properties.create(Material.IRON)
                            .hardnessAndResistance(4, 20)
                            .sound(SoundType.METAL)));

    public static final BlockRegistryObject<FlaxPlant> FLAX_PLANT = registerNoItem("flax_plant", () ->
            new FlaxPlant(AbstractBlock.Properties.create(Material.PLANTS)
                    .hardnessAndResistance(0)
                    .doesNotBlockMovement()
                    .tickRandomly()
                    .sound(SoundType.CROP)));
    public static final BlockRegistryObject<BushBlock> WILD_FLAX_PLANT = registerNoItem("wild_flax_plant", () ->
            new BushBlock(AbstractBlock.Properties.create(Material.PLANTS)
                    .hardnessAndResistance(0)
                    .doesNotBlockMovement()
                    .sound(SoundType.CROP)));
    public static final BlockRegistryObject<TorchBlock> STONE_TORCH = register("stone_torch",
            () -> new TorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS)
                    .doesNotBlockMovement()
                    .hardnessAndResistance(0)
                    .setLightLevel(state -> 14)
                    .sound(SoundType.STONE),
                    ParticleTypes.FLAME),
            bro -> getStoneTorchItem());
    public static final BlockRegistryObject<WallTorchBlock> WALL_STONE_TORCH = registerNoItem("wall_stone_torch", () ->
            new WallTorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS)
                    .doesNotBlockMovement()
                    .hardnessAndResistance(0)
                    .setLightLevel(state -> 14)
                    .sound(SoundType.STONE)
                    .lootFrom(STONE_TORCH.get()),
                    ParticleTypes.FLAME));

    public static final BlockRegistryObject<Block> NETHERWOOD_CHARCOAL_BLOCK = register("netherwood_charcoal_block",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK)
                    .setRequiresTool()
                    .hardnessAndResistance(5, 6)),
            bro -> () -> new BlockItem(bro.get(), new Item.Properties().group(SilentGear.ITEM_GROUP)) {
                @Override
                public int getBurnTime(ItemStack itemStack) {
                    return 10 * Config.Common.netherwoodCharcoalBurnTime.get();
                }
            });

    public static final BlockRegistryObject<WoodBlock> NETHERWOOD_LOG = register("netherwood_log", () ->
            new WoodBlock(STRIPPED_WOOD::get, netherWoodProps(2f, 2f)));
    public static final BlockRegistryObject<RotatedPillarBlock> STRIPPED_NETHERWOOD_LOG = register("stripped_netherwood_log", () ->
            new RotatedPillarBlock(netherWoodProps(2f, 2f)));
    public static final BlockRegistryObject<WoodBlock> NETHERWOOD_WOOD = register("netherwood_wood", () ->
            new WoodBlock(STRIPPED_WOOD::get, netherWoodProps(2f, 2f)));
    public static final BlockRegistryObject<RotatedPillarBlock> STRIPPED_NETHERWOOD_WOOD = register("stripped_netherwood_wood", () ->
            new RotatedPillarBlock(netherWoodProps(2f, 2f)));

    public static final BlockRegistryObject<Block> NETHERWOOD_PLANKS = register("netherwood_planks", () ->
            new Block(netherWoodProps(2f, 3f)));
    public static final BlockRegistryObject<SlabBlock> NETHERWOOD_SLAB = register("netherwood_slab", () ->
            new SlabBlock(netherWoodProps(2f, 3f)));
    public static final BlockRegistryObject<StairsBlock> NETHERWOOD_STAIRS = register("netherwood_stairs", () ->
            new StairsBlock(NETHERWOOD_PLANKS::asBlockState, netherWoodProps(2f, 3f)));
    public static final BlockRegistryObject<FenceBlock> NETHERWOOD_FENCE = register("netherwood_fence", () ->
            new FenceBlock(netherWoodProps(2f, 3f)));
    public static final BlockRegistryObject<FenceGateBlock> NETHERWOOD_FENCE_GATE = register("netherwood_fence_gate", () ->
            new FenceGateBlock(netherWoodProps(2f, 3f)));
    public static final BlockRegistryObject<DoorBlock> NETHERWOOD_DOOR = register("netherwood_door", () ->
            new DoorBlock(netherWoodProps(3f, 3f).notSolid()));
    public static final BlockRegistryObject<TrapDoorBlock> NETHERWOOD_TRAPDOOR = register("netherwood_trapdoor", () ->
            new TrapDoorBlock(netherWoodProps(3f, 3f).notSolid()));
    public static final BlockRegistryObject<LeavesBlock> NETHERWOOD_LEAVES = register("netherwood_leaves", () ->
            new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES)
                    .hardnessAndResistance(0.2f)
                    .tickRandomly()
                    .notSolid()
                    .sound(SoundType.PLANT)));
    public static final BlockRegistryObject<NetherwoodSapling> NETHERWOOD_SAPLING = register("netherwood_sapling", () ->
            new NetherwoodSapling(AbstractBlock.Properties.create(Material.PLANTS)
                    .hardnessAndResistance(0)
                    .doesNotBlockMovement()
                    .tickRandomly()
                    .sound(SoundType.PLANT)));

    public static final BlockRegistryObject<FlowerPotBlock> POTTED_NETHERWOOD_SAPLING = registerNoItem("potted_netherwood_sapling", () ->
            makePottedPlant(NETHERWOOD_SAPLING));
    public static final BlockRegistryObject<PhantomLight> PHANTOM_LIGHT = register("phantom_light", PhantomLight::new);

    private ModBlocks() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderTypes(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(FLAX_PLANT.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(NETHERWOOD_DOOR.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(NETHERWOOD_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(NETHERWOOD_TRAPDOOR.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_NETHERWOOD_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(STONE_TORCH.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WALL_STONE_TORCH.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WILD_FLAX_PLANT.get(), RenderType.getCutout());
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        STRIPPED_WOOD.put(NETHERWOOD_LOG.get(), STRIPPED_NETHERWOOD_LOG.get());
        STRIPPED_WOOD.put(NETHERWOOD_WOOD.get(), STRIPPED_NETHERWOOD_WOOD.get());
    }

    private static OreBlock getOre(int harvestLevel, SoundType soundType) {
        return new OreBlock(AbstractBlock.Properties.create(Material.ROCK)
                .hardnessAndResistance(4, 10)
                .setRequiresTool()
                .harvestLevel(harvestLevel)
                .harvestTool(ToolType.PICKAXE)
                .sound(soundType));
    }

    private static Block getStorageBlock() {
        return new Block(AbstractBlock.Properties.create(Material.IRON)
                .hardnessAndResistance(3.0f, 6.0f)
                .sound(SoundType.METAL));
    }

    private static <T extends Block> BlockRegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return new BlockRegistryObject<>(Registration.BLOCKS.register(name, block));
    }

    private static <T extends Block> BlockRegistryObject<T> register(String name, Supplier<T> block) {
        return register(name, block, ModBlocks::defaultItem);
    }

    private static <T extends Block> BlockRegistryObject<T> register(String name, Supplier<T> block, Function<BlockRegistryObject<T>, Supplier<? extends BlockItem>> item) {
        BlockRegistryObject<T> ret = registerNoItem(name, block);
        Registration.ITEMS.register(name, item.apply(ret));
        return ret;
    }

    private static <T extends Block> Supplier<BlockItem> defaultItem(BlockRegistryObject<T> block) {
        return () -> new BlockItem(block.get(), new Item.Properties().group(SilentGear.ITEM_GROUP));
    }

    private static Supplier<BlockItem> getStoneTorchItem() {
        return () -> new WallOrFloorItem(STONE_TORCH.get(), WALL_STONE_TORCH.get(), new Item.Properties().group(SilentGear.ITEM_GROUP));
    }

    @SuppressWarnings("SameParameterValue")
    private static FlowerPotBlock makePottedPlant(Supplier<? extends Block> flower) {
        FlowerPotBlock potted = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT.delegate.get(), flower, Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0));
        ResourceLocation flowerId = Objects.requireNonNull(flower.get().getRegistryName());
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(flowerId, () -> potted);
        return potted;
    }

    private static AbstractBlock.Properties netherWoodProps(float hardnessIn, float resistanceIn) {
        return AbstractBlock.Properties.create(Material.NETHER_WOOD)
                .harvestTool(ToolType.AXE)
                .hardnessAndResistance(hardnessIn, resistanceIn)
                .sound(SoundType.WOOD);
    }
}

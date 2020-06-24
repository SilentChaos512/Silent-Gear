package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.*;
import net.silentchaos512.gear.block.craftingstation.CraftingStationBlock;
import net.silentchaos512.gear.block.grader.GraderBlock;
import net.silentchaos512.gear.block.salvager.SalvagerBlock;
import net.silentchaos512.lib.registry.BlockRegistryObject;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {
    public static final BlockRegistryObject<CrimsonIronOre> CRIMSON_IRON_ORE = register("crimson_iron_ore", CrimsonIronOre::new);
    public static final BlockRegistryObject<MetalBlock> CRIMSON_IRON_BLOCK = register("crimson_iron_block", () -> new MetalBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final BlockRegistryObject<MetalBlock> CRIMSON_STEEL_BLOCK = register("crimson_steel_block", () -> new MetalBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final BlockRegistryObject<MetalBlock> BLAZE_GOLD_BLOCK = register("blaze_gold_block", () -> new MetalBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final BlockRegistryObject<GraderBlock> MATERIAL_GRADER = register("material_grader", () -> new GraderBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 30)));
    public static final BlockRegistryObject<CraftingStationBlock> CRAFTING_STATION = register("crafting_station", CraftingStationBlock::new);
    public static final BlockRegistryObject<SalvagerBlock> SALVAGER = register("salvager", SalvagerBlock::new);
    public static final BlockRegistryObject<FlaxPlant> FLAX_PLANT = registerNoItem("flax_plant", () -> new FlaxPlant(false));
    public static final BlockRegistryObject<FlaxPlant> WILD_FLAX_PLANT = registerNoItem("wild_flax_plant", () -> new FlaxPlant(true));
    public static final BlockRegistryObject<StoneTorch> STONE_TORCH = register("stone_torch", StoneTorch::new, bro -> getStoneTorchItem());
    public static final BlockRegistryObject<StoneTorchWall> WALL_STONE_TORCH = registerNoItem("wall_stone_torch", StoneTorchWall::new);
    public static final BlockRegistryObject<NetherwoodLog> NETHERWOOD_LOG = register("netherwood_log", NetherwoodLog::new);
    public static final BlockRegistryObject<NetherwoodPlanks> NETHERWOOD_PLANKS = register("netherwood_planks", NetherwoodPlanks::new);
    public static final BlockRegistryObject<NetherwoodSlab> NETHERWOOD_SLAB = register("netherwood_slab", NetherwoodSlab::new);
    public static final BlockRegistryObject<NetherwoodStairs> NETHERWOOD_STAIRS = register("netherwood_stairs", NetherwoodStairs::new);
    public static final BlockRegistryObject<NetherwoodLeaves> NETHERWOOD_LEAVES = register("netherwood_leaves", NetherwoodLeaves::new);
    public static final BlockRegistryObject<NetherwoodSapling> NETHERWOOD_SAPLING = register("netherwood_sapling", NetherwoodSapling::new);
    public static final BlockRegistryObject<FlowerPotBlock> POTTED_NETHERWOOD_SAPLING = registerNoItem("potted_netherwood_sapling", () -> makePottedPlant(NETHERWOOD_SAPLING));
    public static final BlockRegistryObject<PhantomLight> PHANTOM_LIGHT = register("phantom_light", PhantomLight::new);

    private ModBlocks() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderTypes(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(FLAX_PLANT.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(NETHERWOOD_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_NETHERWOOD_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(STONE_TORCH.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WALL_STONE_TORCH.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WILD_FLAX_PLANT.get(), RenderType.getCutout());
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

    private static FlowerPotBlock makePottedPlant(Supplier<? extends Block> flower) {
        FlowerPotBlock potted = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT.delegate.get(), flower, Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0));
        ResourceLocation flowerId = Objects.requireNonNull(flower.get().getRegistryName());
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(flowerId, () -> potted);
        return potted;
    }
}

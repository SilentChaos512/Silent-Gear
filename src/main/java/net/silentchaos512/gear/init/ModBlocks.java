package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemWallOrFloor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.*;
import net.silentchaos512.gear.block.analyzer.BlockPartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.gear.block.salvager.BlockSalvager;
import net.silentchaos512.lib.block.IBlockProvider;
import net.silentchaos512.utils.Lazy;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

public enum ModBlocks implements IBlockProvider, IStringSerializable {
    CRAFTING_STATION(BlockCraftingStation::new),
    PART_ANALYZER(BlockPartAnalyzer::new),
    SALVAGER(BlockSalvager::new),
    FLOWER(Flower::new),
    FLAX_PLANT(() -> new FlaxPlant(false), () -> null),
    WILD_FLAX_PLANT(() -> new FlaxPlant(true), () -> null),
    STONE_TORCH(StoneTorch::new, ModBlocks::getStoneTorchItem),
    WALL_STONE_TORCH(StoneTorchWall::new, () -> null),
    NETHERWOOD_LOG(NetherwoodLog::new),
    NETHERWOOD_PLANKS(NetherwoodPlanks::new),
    NETHERWOOD_LEAVES(NetherwoodLeaves::new),
    NETHERWOOD_SAPLING(NetherwoodSapling::new),
    CRIMSON_IRON_ORE(CrimsonIronOre::new),
    POTTED_FLOWER(() -> makePottedPlant(FLOWER), () -> null),
    POTTED_NETHERWOOD_SAPLING(() -> makePottedPlant(NETHERWOOD_SAPLING), () -> null),
    PHANTOM_LIGHT(PhantomLight::new);

    private final Lazy<Block> block;

    private final Lazy<ItemBlock> item;
    ModBlocks(Supplier<Block> blockSupplier) {
        this.block = Lazy.of(blockSupplier);
        this.item = Lazy.of(() -> new ItemBlock(this.asBlock(), new Item.Properties().group(SilentGear.ITEM_GROUP)));
    }

    ModBlocks(Supplier<Block> blockSupplier, Supplier<ItemBlock> itemBlockSupplier) {
        this.block = Lazy.of(blockSupplier);
        this.item = Lazy.of(itemBlockSupplier);
    }

    public static void registerAll(RegistryEvent.Register<Block> event) {
        if (!event.getName().equals(ForgeRegistries.BLOCKS.getRegistryName())) return;

        for (ModBlocks block : values()) {
            register(block.getName(), block.asBlock(), block.item.get());
        }
    }

    private static void register(String name, Block block, @Nullable ItemBlock item) {
        ResourceLocation registryName = new ResourceLocation(SilentGear.MOD_ID, name);
        block.setRegistryName(registryName);
        ForgeRegistries.BLOCKS.register(block);
        if (item != null) {
            ModItems.blocksToRegister.put(name, item);
        }
    }

    private static ItemBlock getStoneTorchItem() {
        return new ItemWallOrFloor(STONE_TORCH.asBlock(), WALL_STONE_TORCH.asBlock(), new Item.Properties());
    }

    private static BlockFlowerPot makePottedPlant(IBlockProvider flower) {
        return new BlockFlowerPot(flower.asBlock(), Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(0));
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Block asBlock() {
        return block.get();
    }

    @Override
    public Item asItem() {
        return asBlock().asItem();
    }
}

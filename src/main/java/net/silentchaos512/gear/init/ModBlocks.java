package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.*;
import net.silentchaos512.gear.block.analyzer.BlockPartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.gear.block.salvager.BlockSalvager;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModBlocks implements IBlockProvider, IStringSerializable {
    CRAFTING_STATION(BlockCraftingStation::new),
    PART_ANALYZER(BlockPartAnalyzer::new),
    SALVAGER(BlockSalvager::new),
    FLOWER(Flower::new),
    FLAX_PLANT(FlaxPlant::new),
    NETHERWOOD_LOG(NetherwoodLog::new),
    NETHERWOOD_PLANKS(NetherwoodPlanks::new),
    NETHERWOOD_LEAVES(NetherwoodLeaves::new),
    NETHERWOOD_SAPLING(NetherwoodSapling::new),
    CRIMSON_IRON_ORE(CrimsonIronOre::new),
    PHANTOM_LIGHT(PhantomLight::new);

    private final LazyLoadBase<Block> block;

    ModBlocks(Supplier<Block> blockSupplier) {
        this.block = new LazyLoadBase<>(blockSupplier);
    }

    public static void registerAll(RegistryEvent.Register<Block> event) {
        if (!event.getName().equals(ForgeRegistries.BLOCKS.getRegistryName())) return;

        for (ModBlocks block : values()) {
            register(block.getName(), block.asBlock());
        }
    }

    private static void register(String name, Block block) {
        register(name, block, new ItemBlock(block, new Item.Builder().group(SilentGear.ITEM_GROUP)));
    }

    private static void register(String name, Block block, ItemBlock item) {
        ResourceLocation registryName = new ResourceLocation(SilentGear.MOD_ID, name);
        block.setRegistryName(registryName);
        ForgeRegistries.BLOCKS.register(block);
        ModItems.blocksToRegister.put(name, item);
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Block asBlock() {
        return block.getValue();
    }

    @Override
    public Item asItem() {
        return asBlock().asItem();
    }
}

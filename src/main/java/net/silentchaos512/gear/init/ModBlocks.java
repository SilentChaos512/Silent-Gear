package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.block.analyzer.BlockPartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.gear.block.Flower;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModBlocks implements IRegistrationHandler<Block> {

    public static final ModBlocks INSTANCE = new ModBlocks();

    public static BlockCraftingStation craftingStation = new BlockCraftingStation();
    public static BlockPartAnalyzer partAnalyzer = new BlockPartAnalyzer();
    public static Flower flower = new Flower();
    public static FlaxPlant flaxPlant = new FlaxPlant();

    @Override
    public void registerAll(SRegistry reg) {
        registerBlockStandardItem(reg, craftingStation, "crafting_station");
        registerBlockStandardItem(reg, partAnalyzer, "part_analyzer");
        registerBlockStandardItem(reg, flower, "flower");
        registerBlockStandardItem(reg, flaxPlant, "flax_plant");
    }

    // Until 1.13, SRegistry doesn't handle this correctly right now and it can't be fixed.
    private void registerBlockStandardItem(SRegistry reg, Block block, String name) {
        reg.registerBlock(block, name, new ItemBlock(block));
    }
}

package net.silentchaos512.gear.init;

import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.block.Flower;
import net.silentchaos512.gear.block.analyzer.BlockPartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.gear.block.salvager.BlockSalvager;
import net.silentchaos512.lib.registry.SRegistry;

public class ModBlocks {
    public static BlockCraftingStation craftingStation = new BlockCraftingStation();
    public static BlockPartAnalyzer partAnalyzer = new BlockPartAnalyzer();
    public static BlockSalvager salvager = new BlockSalvager();
    public static Flower flower = new Flower();
    public static FlaxPlant flaxPlant = new FlaxPlant();

    public static void registerAll(SRegistry reg) {
        reg.registerBlock(craftingStation, "crafting_station");
        reg.registerBlock(partAnalyzer, "part_analyzer");
        reg.registerBlock(salvager, "salvager");
        reg.registerBlock(flower, "flower");
        reg.registerBlock(flaxPlant, "flax_plant");
    }
}

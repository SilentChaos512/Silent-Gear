package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.silentchaos512.gear.block.*;
import net.silentchaos512.gear.block.analyzer.BlockPartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.gear.block.salvager.BlockSalvager;
import net.silentchaos512.lib.registry.SRegistry;

public final class ModBlocks {
    public static BlockCraftingStation craftingStation = new BlockCraftingStation();
    public static BlockPartAnalyzer partAnalyzer = new BlockPartAnalyzer();
    public static BlockSalvager salvager = new BlockSalvager();
    public static Flower flower = new Flower();
    public static FlaxPlant flaxPlant = new FlaxPlant();
    public static NetherwoodSapling netherwoodSapling = new NetherwoodSapling();
    public static NetherwoodLog netherwoodLog = new NetherwoodLog();
    public static NetherwoodLeaves netherwoodLeaves = new NetherwoodLeaves();
    public static NetherwoodPlanks netherwoodPlanks = new NetherwoodPlanks();
    public static Block crimsonIronOre = new BlockOre().setHardness(4).setResistance(10);
    public static Block phantomLight = new PhantomLight();

    private ModBlocks() {}

    public static void registerAll(SRegistry reg) {
        reg.registerBlock(crimsonIronOre, "crimson_iron_ore");
        reg.registerBlock(craftingStation, "crafting_station");
        reg.registerBlock(partAnalyzer, "part_analyzer");
        reg.registerBlock(salvager, "salvager");
        reg.registerBlock(netherwoodSapling, "netherwood_sapling");
        reg.registerBlock(netherwoodLog, "netherwood_log");
        reg.registerBlock(netherwoodLeaves, "netherwood_leaves");
        reg.registerBlock(netherwoodPlanks, "netherwood_planks");
        reg.registerBlock(flower, "flower");
        reg.registerBlock(flaxPlant, "flax_plant");
        reg.registerBlock(phantomLight, "phantom_light");

//        if (GameUtil.isDeobfuscated()) {
//            Block block = new BlockFalling(Material.SAND);
//            block.setHardness(3);
//            block.setHarvestLevel("shovel", 1);
//            reg.registerBlock(block, "test_block");
//        }
    }
}

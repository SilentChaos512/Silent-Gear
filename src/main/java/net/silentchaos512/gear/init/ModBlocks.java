package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.block.Flower;
import net.silentchaos512.gear.block.analyzer.BlockPartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.GameUtil;

public class ModBlocks {
    public static BlockCraftingStation craftingStation = new BlockCraftingStation();
    public static BlockPartAnalyzer partAnalyzer = new BlockPartAnalyzer();
    public static Flower flower = new Flower();
    public static FlaxPlant flaxPlant = new FlaxPlant();

    public static void registerAll(SRegistry reg) {
        reg.registerBlock(craftingStation, "crafting_station");
        reg.registerBlock(partAnalyzer, "part_analyzer");
        reg.registerBlock(flower, "flower");
        reg.registerBlock(flaxPlant, "flax_plant");

        if (GameUtil.isDeobfuscated()) {
            Block block = new BlockFalling(Material.SAND);
            block.setHardness(3);
            block.setHarvestLevel("shovel", 1);
            reg.registerBlock(block, "test_block");
        }
    }
}

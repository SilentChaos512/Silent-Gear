package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.block.Flower;
import net.silentchaos512.gear.block.craftingstation.BlockCraftingStation;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModBlocks implements IRegistrationHandler<Block> {

    public static final ModBlocks INSTANCE = new ModBlocks();

    public static BlockCraftingStation craftingStation = new BlockCraftingStation();
    public static Flower flower = new Flower();
    public static FlaxPlant flaxPlant = new FlaxPlant();

    @Override
    public void registerAll(SRegistry reg) {
        reg.registerBlock(craftingStation);
        reg.registerBlock(flower);
        reg.registerBlock(flaxPlant);
    }
}

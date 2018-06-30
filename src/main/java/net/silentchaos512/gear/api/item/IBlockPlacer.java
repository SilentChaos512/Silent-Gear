package net.silentchaos512.gear.api.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public interface IBlockPlacer {

    IBlockState getBlockPlaced(ItemStack stack);

    int getRemainingBlocks(ItemStack stack);
}

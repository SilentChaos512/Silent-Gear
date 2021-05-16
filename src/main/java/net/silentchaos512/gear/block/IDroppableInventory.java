package net.silentchaos512.gear.block;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * An inventory whose dropped items may not match their true inventory
 */
public interface IDroppableInventory {
    NonNullList<ItemStack> getItemsToDrop();
}

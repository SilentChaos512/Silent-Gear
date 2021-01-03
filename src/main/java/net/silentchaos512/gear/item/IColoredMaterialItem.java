package net.silentchaos512.gear.item;

import net.minecraft.item.ItemStack;

public interface IColoredMaterialItem {
    int getColor(ItemStack stack, int layer);
}

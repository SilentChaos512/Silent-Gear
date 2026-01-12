package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;

@Deprecated
public interface IColoredMaterialItem {
    /**
     * Gets the color of the material
     *
     * @param stack The item
     * @param layer The tint index
     * @return The color of the material for the tint index
     */
    int getColor(ItemStack stack, int layer);
}

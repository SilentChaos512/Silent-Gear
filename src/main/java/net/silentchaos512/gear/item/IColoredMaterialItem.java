package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import javax.annotation.Nullable;

public interface IColoredMaterialItem {
    /**
     * Unused
     *
     * @param stack The item
     * @return The material the item is made of
     * @deprecated No longer used, to be removed
     */
    @Deprecated(forRemoval = true)
    @Nullable
    MaterialInstance getPrimarySubMaterial(ItemStack stack);

    /**
     * Gets the color of the material
     *
     * @param stack The item
     * @param layer The tint index
     * @return The color of the material for the tint index
     */
    int getColor(ItemStack stack, int layer);
}

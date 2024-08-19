package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import javax.annotation.Nullable;

public interface IColoredMaterialItem {
    @Nullable
    MaterialInstance getPrimarySubMaterial(ItemStack stack);

    int getColor(ItemStack stack, int layer);
}

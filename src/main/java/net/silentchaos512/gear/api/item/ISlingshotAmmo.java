package net.silentchaos512.gear.api.item;

import net.minecraft.world.item.ItemStack;

public interface ISlingshotAmmo {
    default boolean isAmmo(ItemStack stack) {
        return true;
    }
}

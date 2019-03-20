package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.ItemStack;

public interface IBlueprint {
    default boolean isSingleUse(ItemStack blueprint) {
        return false;
    }
}

package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface IBlueprint {
    @Deprecated
    ItemStack getCraftingResult(ItemStack blueprint, Collection<ItemStack> parts);

    @Deprecated
    int getMaterialCost(ItemStack blueprint);

    default boolean isSingleUse(ItemStack blueprint) {
        return false;
    }
}

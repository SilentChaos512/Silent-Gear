package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface IBlueprint {
    ItemStack getCraftingResult(ItemStack blueprint, Collection<ItemStack> parts);

    int getMaterialCost(ItemStack blueprint);

    default boolean isSingleUse(ItemStack blueprint) {
        return false;
    }
}

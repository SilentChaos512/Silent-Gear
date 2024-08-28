package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.silentchaos512.gear.setup.SgDataComponents;

public interface IContainerItem {
    int getInventorySize(ItemStack stack);

    boolean canStore(ItemStack stack);

    default ComponentItemHandler getInventory(ItemStack stack) {
        return new ComponentItemHandler(stack, SgDataComponents.CONTAINED_ITEMS.get(), getInventorySize(stack));
    }

    default int getInventoryRows(ItemStack stack) {
        return getInventorySize(stack) / 9;
    }
}

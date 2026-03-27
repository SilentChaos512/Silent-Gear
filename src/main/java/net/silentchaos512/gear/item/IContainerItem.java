package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.silentchaos512.gear.setup.SgDataComponents;

public interface IContainerItem {
    int getInventorySize(ItemInstance instance);

    boolean canStore(ItemStack stack);

    default ComponentItemHandler getInventory(ItemStack stack) {
        return new ComponentItemHandler(stack, SgDataComponents.CONTAINED_ITEMS.get(), getInventorySize(stack));
    }

    default int getInventoryRows(ItemInstance instance) {
        return getInventorySize(instance) / 9;
    }
}

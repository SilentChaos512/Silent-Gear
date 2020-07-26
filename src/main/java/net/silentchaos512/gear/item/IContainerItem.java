package net.silentchaos512.gear.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public interface IContainerItem {
    int getInventorySize(ItemStack stack);

    boolean canStore(ItemStack stack);

    default IItemHandler getInventory(ItemStack stack) {
        ItemStackHandler stackHandler = new ItemStackHandler(getInventorySize(stack));
        stackHandler.deserializeNBT(stack.getOrCreateChildTag("Inventory"));
        return stackHandler;
    }

    default void saveInventory(ItemStack stack, IItemHandler itemHandler, PlayerEntity player) {
        if (itemHandler instanceof ItemStackHandler) {
            stack.getOrCreateTag().put("Inventory", ((ItemStackHandler) itemHandler).serializeNBT());
        }
    }

    default int getInventoryRows(ItemStack stack) {
        return getInventorySize(stack) / 9;
    }
}

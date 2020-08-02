package net.silentchaos512.gear.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public interface IContainerItem {
    int getInventorySize(ItemStack stack);

    boolean canStore(ItemStack stack);

    default IItemHandler getInventory(ItemStack stack) {
        ItemStackHandler stackHandler = new ItemStackHandler(getInventorySize(stack));
        CompoundNBT nbt = stack.getOrCreateChildTag("Inventory");
        // Allow older blueprint books to update to new size
        nbt.remove("Size");
        stackHandler.deserializeNBT(nbt);
        return stackHandler;
    }

    default void saveInventory(ItemStack stack, IItemHandler itemHandler) {
        if (itemHandler instanceof ItemStackHandler) {
            stack.getOrCreateTag().put("Inventory", ((ItemStackHandler) itemHandler).serializeNBT());
        }
    }

    default int getInventoryRows(ItemStack stack) {
        return getInventorySize(stack) / 9;
    }
}

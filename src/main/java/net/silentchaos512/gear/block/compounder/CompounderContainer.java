package net.silentchaos512.gear.block.compounder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;

import java.util.Collection;

public class CompounderContainer extends Container {
    private final IInventory inventory;
    final IIntArray fields;

    public CompounderContainer(ContainerType<?> containerType, int id, PlayerInventory playerInventory, PacketBuffer buffer, Collection<IMaterialCategory> categories) {
        this(containerType, id, playerInventory, new Inventory(buffer.readByte()), new IntArray(2), categories);
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public CompounderContainer(ContainerType<?> containerType, int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields, Collection<IMaterialCategory> categories) {
        super(containerType, id);
        this.inventory = inventory;
        this.fields = fields;

        //assertInventorySize(this.inventory, CompounderTileEntity.INVENTORY_SIZE);

        for (int i = 0; i < this.inventory.getSizeInventory() - 1; ++i) {
            addSlot(new Slot(this.inventory, i, 17 + 18 * i, 35) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return CompounderTileEntity.canAcceptInput(stack, categories);
                }
            });
        }
        addSlot(new SlotOutputOnly(this.inventory, this.inventory.getSizeInventory() - 1, 126, 35));

        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        trackIntArray(this.fields);
    }

    public int getProgressArrowScale() {
        int progress = fields.get(0);
        return progress != 0 ? progress * 24 / CompounderTileEntity.WORK_TIME : 0;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllContents(this, getInventory());
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return inventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            final int size = inventory.getSizeInventory();
            final int startPlayer = size;
            final int endPlayer = size + 27;
            final int startHotbar = size + 27;
            final int endHotbar = size + 36;
            int outputSlot = size - 1;

            if (index == outputSlot) {
                // Remove from output slot?
                if (!this.mergeItemStack(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < outputSlot && inventory.isItemValidForSlot(index, stack1)) {
                // Move from player to input slots?
                if (!mergeItemStack(stack1, 0, outputSlot, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= startPlayer && index < endPlayer) {
                // Move player items to hotbar.
                if (!mergeItemStack(stack1, startHotbar, endHotbar, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= startHotbar && index < endHotbar) {
                // Move player items from hotbar.
                if (!mergeItemStack(stack1, startPlayer, endPlayer, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack1, startPlayer, endHotbar, false)) {
                return ItemStack.EMPTY;
            }

            if (stack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack1.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack1);
        }

        return stack;
    }
}

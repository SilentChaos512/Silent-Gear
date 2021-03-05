package net.silentchaos512.gear.block.compounder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.network.CompounderUpdatePacket;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;

import java.util.Collection;

public class CompounderContainer extends Container {
    private final IInventory inventory;
    private final IIntArray fields;

    public CompounderContainer(ContainerType<?> containerType, int id, PlayerInventory playerInventory, PacketBuffer buffer, Collection<IMaterialCategory> categories) {
        this(containerType, id, playerInventory, new Inventory(buffer.readByte()), new IntArray(buffer.readByte()), categories);
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public CompounderContainer(ContainerType<?> containerType, int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields, Collection<IMaterialCategory> categories) {
        super(containerType, id);
        this.inventory = inventory;
        this.fields = fields;

        //assertInventorySize(this.inventory, CompounderTileEntity.INVENTORY_SIZE);

        for (int i = 0; i < this.inventory.getSizeInventory() - 2; ++i) {
            addSlot(new Slot(this.inventory, i, 17 + 18 * i, 35) /*{
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return CompounderTileEntity.canAcceptInput(stack, categories);
                }
            }*/);
        }
        addSlot(new SlotOutputOnly(this.inventory, this.inventory.getSizeInventory() - 2, 126, 35));
        addSlot(new SlotOutputOnly(this.inventory, this.inventory.getSizeInventory() - 1, 126, 60) {
            @Override
            public boolean canTakeStack(PlayerEntity playerIn) {
                return false;
            }
        });

        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        trackIntArray(this.fields);
    }

    boolean getWorkEnabled() {
        return this.fields.get(1) != 0;
    }

    public void setWorkEnabled(boolean value) {
        this.fields.set(1, value ? 1 : 0);
    }

    void toggleWorkEnabled() {
        this.fields.set(1, this.fields.get(1) == 0 ? 1 : 0);
        Network.channel.sendToServer(new CompounderUpdatePacket(getWorkEnabled()));
    }

    public int getProgressArrowScale() {
        int progress = fields.get(0);
        return progress != 0 ? progress * 24 / CompounderTileEntity.WORK_TIME : 0;
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
            final int inventorySize = inventory.getSizeInventory();
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;
            int outputSlot = inventorySize - 2;

            if (index == outputSlot) {
                // Move output to player
                if (!this.mergeItemStack(stack1, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack1, stack);
            } else if (index >= inventorySize) {
                if (isValidIngredient()) {
                    if (!this.mergeItemStack(stack1, 0, outputSlot, false)) {
                        // Move from player or hotbar to input slots
                        return ItemStack.EMPTY;
                    }
                } else if (index < playerInventoryEnd) {
                    if (!this.mergeItemStack(stack1, playerInventoryEnd, playerHotbarEnd, false)) {
                        // Move from player to hotbar
                        return ItemStack.EMPTY;
                    }
                } else if (index < playerHotbarEnd && !this.mergeItemStack(stack1, inventorySize, playerInventoryEnd, false)) {
                    // Move from hotbar to player
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack1, inventorySize, playerHotbarEnd, false)) {
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

    private boolean isValidIngredient() {
        return true; // TODO
    }
}

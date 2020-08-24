package net.silentchaos512.gear.block.grader;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.init.ModContainers;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.utils.EnumUtils;

public class GraderContainer extends Container {
    private final IInventory inventory;
    final IIntArray fields;

    public GraderContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(id, playerInventory, new Inventory(GraderTileEntity.INVENTORY_SIZE), new IntArray(2));
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public GraderContainer(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields) {
        super(ModContainers.MATERIAL_GRADER.get(), id);
        this.inventory = inventory;
        this.fields = fields;

        assertInventorySize(this.inventory, GraderTileEntity.INVENTORY_SIZE);

        addSlot(new Slot(inventory, 0, 26, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return GraderTileEntity.canAcceptInput(stack);
            }
        });
        addSlot(new Slot(inventory, 1, 26, 55) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return GraderTileEntity.getCatalystTier(stack) > 0;
            }
        });
        addSlot(new SlotOutputOnly(inventory, 2, 80, 35));
        addSlot(new SlotOutputOnly(inventory, 3, 98, 35));
        addSlot(new SlotOutputOnly(inventory, 4, 116, 35));
        addSlot(new SlotOutputOnly(inventory, 5, 134, 35));

        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        trackIntArray(this.fields);
    }

    public int getProgressArrowScale() {
        int progress = fields.get(0);
        return progress != 0 ? progress * 24 / GraderTileEntity.BASE_ANALYZE_TIME : 0;
    }

    public MaterialGrade getLastGradeAttempt() {
        return EnumUtils.byOrdinal(fields.get(1), MaterialGrade.NONE);
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

            if (index >= 2 && index < GraderTileEntity.INVENTORY_SIZE) {
                // Remove from output slot?
                if (!this.mergeItemStack(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.isItemValidForSlot(GraderTileEntity.INPUT_SLOT, stack1)) {
                // Move from player to input slot?
                if (!mergeItemStack(stack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.isItemValidForSlot(GraderTileEntity.CATALYST_SLOT, stack1)) {
                // Move from player to catalyst slot?
                if (!mergeItemStack(stack1, 1, 2, false)) {
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

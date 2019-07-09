/*
 * Silent Gear -- PartAnalyzerContainer
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.block.analyzer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.silentchaos512.gear.init.ModContainers;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;

public class PartAnalyzerContainer extends Container {
    private final IInventory inventory;
    final IIntArray fields;

    public PartAnalyzerContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(PartAnalyzerTileEntity.INVENTORY_SIZE), new IntArray(1));
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public PartAnalyzerContainer(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray fields) {
        super(ModContainers.PART_ANALYZER.type(), id);
        this.inventory = inventory;
        this.fields = fields;

        assertInventorySize(this.inventory, PartAnalyzerTileEntity.INVENTORY_SIZE);

        addSlot(new Slot(inventory, 0, 26, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return PartAnalyzerTileEntity.isUngradedMainPart(stack);
            }
        });
        addSlot(new Slot(inventory, 1, 26, 55) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return PartAnalyzerTileEntity.getCatalystTier(stack) > 0;
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
        return progress != 0 ? progress * 24 / PartAnalyzerTileEntity.BASE_ANALYZE_TIME : 0;
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

            if (index >= 2 && index < PartAnalyzerTileEntity.INVENTORY_SIZE) {
                // Remove from output slot?
                if (!this.mergeItemStack(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.isItemValidForSlot(PartAnalyzerTileEntity.INPUT_SLOT, stack1)) {
                // Move from player to input slot?
                if (!mergeItemStack(stack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.isItemValidForSlot(PartAnalyzerTileEntity.CATALYST_SLOT, stack1)) {
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

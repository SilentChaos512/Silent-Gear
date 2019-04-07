/*
 * Silent Gear -- ContainerPartAnalyzer
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.inventory.ContainerSL;
import net.silentchaos512.lib.inventory.SlotOutputOnly;

public class ContainerPartAnalyzer extends ContainerSL {
    public ContainerPartAnalyzer(InventoryPlayer playerInventory, IInventory tileInventory) {
        super(playerInventory, tileInventory);
    }

    @Override
    protected void addTileInventorySlots(IInventory inv) {
        addSlot(new Slot(tileInventory, 0, 26, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return TilePartAnalyzer.isUngradedMainPart(stack);
            }
        });
        addSlot(new Slot(tileInventory, 1, 26, 55) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return TilePartAnalyzer.getCatalystTier(stack) > 0;
            }
        });
        addSlot(new SlotOutputOnly(tileInventory, 2, 80, 35));
        addSlot(new SlotOutputOnly(tileInventory, 3, 98, 35));
        addSlot(new SlotOutputOnly(tileInventory, 4, 116, 35));
        addSlot(new SlotOutputOnly(tileInventory, 5, 134, 35));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllContents(this, getInventory());
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            final int size = tileInventory.getSizeInventory();
            final int startPlayer = size;
            final int endPlayer = size + 27;
            final int startHotbar = size + 27;
            final int endHotbar = size + 36;

            if (index >= 2 && index < TilePartAnalyzer.INVENTORY_SIZE) {
                // Remove from output slot?
                if (!this.mergeItemStack(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && tileInventory.isItemValidForSlot(TilePartAnalyzer.INPUT_SLOT, stack1)) {
                // Move from player to input slot?
                if (!mergeItemStack(stack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && tileInventory.isItemValidForSlot(TilePartAnalyzer.CATALYST_SLOT, stack1)) {
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

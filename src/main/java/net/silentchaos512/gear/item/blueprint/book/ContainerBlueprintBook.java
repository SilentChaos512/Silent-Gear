/*
 * Silent Gear -- ContainerBlueprintBook
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

package net.silentchaos512.gear.item.blueprint.book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.IItemHandler;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerBlueprintBook extends Container {
    @Nonnull private final ItemStack book;
    @Nullable private final IItemHandler itemHandler;

    private int blocked = -1;

    public ContainerBlueprintBook(ItemStack book, InventoryPlayer inventoryPlayer, EnumHand hand) {
        this.book = book;
        this.itemHandler = BlueprintBook.getInventory(book);

        setupSlots(inventoryPlayer, hand);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    private void setupSlots(InventoryPlayer inventoryPlayer, EnumHand hand) {
        setupBookSlots();
        setupPlayerSlots(inventoryPlayer);
    }

    private void setupBookSlots() {
        if (itemHandler == null) {
            SilentGear.log.error("Blueprint book has no inventory? ItemStack: {}", book);
            return;
        }

        for (int row = 0; row < BlueprintBook.INVENTORY_SIZE / 9; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlotToContainer(new SlotBlueprint(itemHandler, col + row * 9, 8 + col * 18, 18 + row * 18));
    }

    private void setupPlayerSlots(InventoryPlayer inventoryPlayer) {
        final int numRows = 3;
        final int hotbarYOffset = (numRows - 4) * 18;

        for (int y = 0; y < numRows; ++y)
            for (int x = 0; x < 9; ++x)
                this.addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + hotbarYOffset));

        for (int x = 0; x < 9; ++x)
            this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 161 + hotbarYOffset));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId < 0 || slotId > inventorySlots.size())
            return super.slotClick(slotId, dragType, clickTypeIn, player);

        Slot slot = inventorySlots.get(slotId);
        if (!canTake(slotId, slot, dragType, player, clickTypeIn))
            return slot.getStack();

        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!(book.getItem() instanceof BlueprintBook)) {
            SilentGear.log.error("Item is not a blueprint book? ItemStack: {}", book);
            SilentGear.log.catching(new IllegalStateException("ContainerBlueprintBook not constructed with BlueprintBook"));
            return;
        }

        BlueprintBook.updateInventory(book, itemHandler, playerIn);
    }

    private boolean canTake(int slotId, Slot slot, int dragType, EntityPlayer player, ClickType clickType) {
        if (slotId == blocked)
            return false;

        if (slotId < BlueprintBook.INVENTORY_SIZE && player.inventory.getItemStack().getItem() instanceof BlueprintBook)
            return false;

        if (clickType == ClickType.SWAP) {
            int hotbarId = BlueprintBook.INVENTORY_SIZE + 27 + dragType;
            if (blocked == hotbarId) return false;

            Slot hotbarSlot = getSlot(hotbarId);
            final boolean eitherIsBlueprintBook = slot.getStack().getItem() instanceof BlueprintBook
                    || hotbarSlot.getStack().getItem() instanceof BlueprintBook;
            return slotId >= BlueprintBook.INVENTORY_SIZE || !eitherIsBlueprintBook;
        }

        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        Slot slot = this.getSlot(slotIndex);

        if (!slot.canTakeStack(player))
            return slot.getStack();

        if (slotIndex == blocked || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack newStack = stack.copy();

        if (slotIndex < BlueprintBook.INVENTORY_SIZE) {
            if (!this.mergeItemStack(stack, BlueprintBook.INVENTORY_SIZE, this.inventorySlots.size(), true))
                return ItemStack.EMPTY;
            slot.onSlotChanged();
        } else if (!this.mergeItemStack(stack, 0, BlueprintBook.INVENTORY_SIZE, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty())
            slot.putStack(ItemStack.EMPTY);
        else
            slot.onSlotChanged();

        return slot.onTake(player, newStack);
    }
}

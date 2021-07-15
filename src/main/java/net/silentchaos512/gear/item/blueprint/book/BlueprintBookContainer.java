package net.silentchaos512.gear.item.blueprint.book;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.silentchaos512.gear.init.ModContainers;
import net.silentchaos512.gear.item.IContainerItem;

import javax.annotation.Nonnull;

public class BlueprintBookContainer extends Container {
    final ItemStack item;
    private final IItemHandler itemHandler;
    int bookSlot = -1;

    public BlueprintBookContainer(int id, PlayerInventory playerInventory, PacketBuffer data) {
        this(id, playerInventory, data.readItem());
    }

    BlueprintBookContainer(int id, PlayerInventory playerInventory, ItemStack stack) {
        super(ModContainers.BLUEPRINT_BOOK.get(), id);
        this.item = stack;
        IContainerItem containerItem = (IContainerItem) this.item.getItem();
        this.itemHandler = containerItem.getInventory(this.item);

        for (int i = 0; i < this.itemHandler.getSlots(); ++i) {
            int x = 8 + 18 * (i % 9);
            int y = 18 + 18 * (i / 9);
            addSlot(new SlotItemHandler(itemHandler, i, x, y) {
                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return containerItem.canStore(stack);
                }
            });
        }

        final int rowCount = this.itemHandler.getSlots() / 9;
        final int yOffset = (rowCount - 4) * 18;

        // Player inventory
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + yOffset));
            }
        }

        // Hotbar
        for (int x = 0; x < 9; ++x) {
            Slot slot = addSlot(new Slot(playerInventory, x, 8 + x * 18, 161 + yOffset) {
                @Override
                public boolean mayPickup(PlayerEntity playerIn) {
                    return index != bookSlot;
                }
            });

            if (x == playerInventory.selected && ItemStack.matches(playerInventory.getSelected(), this.item)) {
                bookSlot = slot.index;
            }
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        Slot slot = this.getSlot(index);

        if (!slot.mayPickup(playerIn))
            return slot.getItem();

        if (index == bookSlot || !slot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack newStack = stack.copy();

        int containerSlots = itemHandler.getSlots();
        if (index < containerSlots) {
            if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true))
                return ItemStack.EMPTY;
            slot.setChanged();
        } else if (!this.moveItemStackTo(stack, 0, containerSlots, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty())
            slot.set(ItemStack.EMPTY);
        else
            slot.setChanged();

        return slot.onTake(playerIn, newStack);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if (slotId < 0 || slotId > slots.size())
            return super.clicked(slotId, dragType, clickTypeIn, player);

        Slot slot = slots.get(slotId);
        if (!canTake(slotId, slot, dragType, player, clickTypeIn))
            return slot.getItem();

        return super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        ((IContainerItem) item.getItem()).saveInventory(item, itemHandler);
    }

    public boolean canTake(int slotId, Slot slot, int button, PlayerEntity player, ClickType clickType) {
        if (slotId == bookSlot || slotId <= itemHandler.getSlots() - 1 && isContainerItem(player.inventory.getCarried()))
            return false;

        // Hotbar swapping via number keys
        if (clickType == ClickType.SWAP) {
            int hotbarId = itemHandler.getSlots() + 27 + button;
            // Block swapping with container
            if (bookSlot == hotbarId)
                return false;

            Slot hotbarSlot = getSlot(hotbarId);
            if (slotId <= itemHandler.getSlots() - 1)
                return !isContainerItem(slot.getItem()) && !isContainerItem(hotbarSlot.getItem());
        }

        return true;
    }

    private static boolean isContainerItem(ItemStack stack) {
        return stack.getItem() instanceof BlueprintBookItem;
    }
}

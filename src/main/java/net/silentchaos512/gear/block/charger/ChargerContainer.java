package net.silentchaos512.gear.block.charger;

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
import net.silentchaos512.gear.api.GearApi;
import net.silentchaos512.gear.init.ModContainers;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.utils.MathUtils;

public class ChargerContainer extends Container {
    private final IInventory inventory;
    private final IIntArray fields;

    public ChargerContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer data) {
        this(type, id, inv, new Inventory(ChargerTileEntity.INVENTORY_SIZE), new IntArray(data.readByte()));
    }

    public ChargerContainer(ContainerType<?> type, int id, PlayerInventory inv, IInventory blockInv, IIntArray fields) {
        super(type, id);
        this.inventory = blockInv;
        this.fields = fields;

        assertInventorySize(this.inventory, ChargerTileEntity.INVENTORY_SIZE);

        addSlot(new Slot(inventory, 0, 56, 23) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return GearApi.isMaterial(stack);
            }
        });
        addSlot(new Slot(inventory, 1, 56, 46) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return true; // FIXME: charging catalyst
            }
        });
        addSlot(new SlotOutputOnly(inventory, 2, 116, 35));

        InventoryUtils.createPlayerSlots(inv, 8, 84).forEach(this::addSlot);

        trackIntArray(this.fields);
    }

    public static ChargerContainer createStarlightCharger(int id, PlayerInventory inv, PacketBuffer data) {
        return new ChargerContainer(ModContainers.STARLIGHT_CHARGER.get(), id, inv, data);
    }

    public static ChargerContainer createStarlightCharger(int id, PlayerInventory inv, IInventory blockInv, IIntArray fields) {
        return new ChargerContainer(ModContainers.STARLIGHT_CHARGER.get(), id, inv, blockInv, fields);
    }

    public int getWorkProgress() {
        return fields.get(0);
    }

    public int getWorkTime() {
        return fields.get(1);
    }

    public int getCharge() {
        return fields.get(2);
    }

    public int getStructureLevel() {
        return fields.get(3);
    }

    public int getMaxCharge() {
        return fields.get(4);
    }

    public int getChargeMeterHeight() {
        int max = getMaxCharge();
        int clamped = MathUtils.clamp(getCharge(), 0, max);
        return max > 0 ? 50 * clamped / max : 0;
    }

    public int getProgressArrowScale() {
        int progress = getWorkProgress();
        int workTime = getWorkTime();
        return progress != 0 && workTime > 0 ? progress * 24 / workTime : 0;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.inventory.isUsableByPlayer(playerIn);
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

            if (index == 2) {
                // Remove from output slot?
                if (!this.mergeItemStack(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.isItemValidForSlot(0, stack1)) {
                // Move from player to input slot?
                if (!mergeItemStack(stack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.isItemValidForSlot(1, stack1)) {
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

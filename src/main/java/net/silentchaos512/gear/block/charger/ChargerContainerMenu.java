package net.silentchaos512.gear.block.charger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgMenuTypes;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.MathUtils;

public class ChargerContainerMenu extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData fields;

    public ChargerContainerMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf data) {
        this(type, id, inv, new SimpleContainer(ChargerBlockEntity.INVENTORY_SIZE), new SimpleContainerData(data.readByte()));
    }

    public ChargerContainerMenu(MenuType<?> type, int id, Inventory inv, Container blockInv, ContainerData fields) {
        super(type, id);
        this.inventory = blockInv;
        this.fields = fields;

        checkContainerSize(this.inventory, ChargerBlockEntity.INVENTORY_SIZE);

        addSlot(new Slot(inventory, 0, 56, 23) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MaterialInstance.from(stack) != null;
            }
        });
        addSlot(new Slot(inventory, 1, 56, 46) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS);
            }
        });
        addSlot(new SlotOutputOnly(inventory, 2, 116, 35));

        InventoryUtils.createPlayerSlots(inv, 8, 84).forEach(this::addSlot);

        addDataSlots(this.fields);
    }

    public static ChargerContainerMenu createStarlightCharger(int id, Inventory inv, FriendlyByteBuf data) {
        return new ChargerContainerMenu(SgMenuTypes.STARLIGHT_CHARGER.get(), id, inv, data);
    }

    public static ChargerContainerMenu createStarlightCharger(int id, Inventory inv, Container blockInv, ContainerData fields) {
        return new ChargerContainerMenu(SgMenuTypes.STARLIGHT_CHARGER.get(), id, inv, blockInv, fields);
    }

    public int getWorkProgress() {
        return fields.get(0);
    }

    public int getWorkTime() {
        return fields.get(1);
    }

    public int getStructureLevel() {
        return fields.get(2);
    }

    public int getCharge() {
        int upper = fields.get(4) & 0xFFFF;
        int lower = fields.get(3) & 0xFFFF;
        return (upper << 16) + lower;
    }

    public int getMaxCharge() {
        int upper = fields.get(6) & 0xFFFF;
        int lower = fields.get(5) & 0xFFFF;
        return (upper << 16) + lower;
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
    public boolean stillValid(Player playerIn) {
        return this.inventory.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            final int size = inventory.getContainerSize();
            final int startPlayer = size;
            final int endPlayer = size + 27;
            final int startHotbar = size + 27;
            final int endHotbar = size + 36;

            if (index == 2) {
                // Remove from output slot?
                if (!this.moveItemStackTo(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.canPlaceItem(0, stack1)) {
                // Move from player to input slot?
                if (!moveItemStackTo(stack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && inventory.canPlaceItem(1, stack1)) {
                // Move from player to catalyst slot?
                if (!moveItemStackTo(stack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= startPlayer && index < endPlayer) {
                // Move player items to hotbar.
                if (!moveItemStackTo(stack1, startHotbar, endHotbar, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= startHotbar && index < endHotbar) {
                // Move player items from hotbar.
                if (!moveItemStackTo(stack1, startPlayer, endPlayer, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack1, startPlayer, endHotbar, false)) {
                return ItemStack.EMPTY;
            }

            if (stack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack1.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack1);
        }

        return stack;
    }
}

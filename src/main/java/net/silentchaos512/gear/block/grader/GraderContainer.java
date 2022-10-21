package net.silentchaos512.gear.block.grader;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.init.SgMenuTypes;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.utils.EnumUtils;

public class GraderContainer extends AbstractContainerMenu {
    private final Container inventory;
    final ContainerData fields;

    public GraderContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, new SimpleContainer(GraderTileEntity.INVENTORY_SIZE), new SimpleContainerData(2));
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public GraderContainer(int id, Inventory playerInventory, Container inventory, ContainerData fields) {
        super(SgMenuTypes.MATERIAL_GRADER.get(), id);
        this.inventory = inventory;
        this.fields = fields;

        checkContainerSize(this.inventory, GraderTileEntity.INVENTORY_SIZE);

        addSlot(new Slot(inventory, 0, 26, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return GraderTileEntity.canAcceptInput(stack);
            }
        });
        addSlot(new Slot(inventory, 1, 26, 55) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return GraderTileEntity.getCatalystTier(stack) > 0;
            }
        });
        addSlot(new SlotOutputOnly(inventory, 2, 80, 35));
        addSlot(new SlotOutputOnly(inventory, 3, 98, 35));
        addSlot(new SlotOutputOnly(inventory, 4, 116, 35));
        addSlot(new SlotOutputOnly(inventory, 5, 134, 35));

        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        addDataSlots(this.fields);
    }

    public int getProgressArrowScale() {
        int progress = fields.get(0);
        return progress != 0 ? progress * 24 / GraderTileEntity.BASE_ANALYZE_TIME : 0;
    }

    public MaterialGrade getLastGradeAttempt() {
        return EnumUtils.byOrdinal(fields.get(1), MaterialGrade.NONE);
    }

    @Override
    public void addSlotListener(ContainerListener listener) {
        super.addSlotListener(listener);
//        listener.refreshContainer(this, getItems()); // FIXME?
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return inventory.stillValid(playerIn);
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

            if (index >= 2 && index < GraderTileEntity.INVENTORY_SIZE) {
                // Remove from output slot?
                if (!this.moveItemStackTo(stack1, startPlayer, endHotbar, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && GraderTileEntity.canAcceptInput(stack1)) {
                // Move from player to input slot?
                if (!moveItemStackTo(stack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= size && GraderTileEntity.getCatalystTier(stack1) > 0) {
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

package net.silentchaos512.gear.block.paintmixer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.block.container.ToggleableWorkMode;
import net.silentchaos512.gear.setup.SgMenuTypes;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;

import javax.annotation.Nullable;

public class PaintMixerMenu extends AbstractContainerMenu implements ToggleableWorkMode {
    private final Container container;
    private final ContainerData data;

    public PaintMixerMenu(int containerId, Inventory playerInventory, @Nullable FriendlyByteBuf buf) {
        this(containerId, playerInventory, new SimpleContainer(PaintMixerBlockEntity.INVENTORY_SIZE), new SimpleContainerData(PaintMixerBlockEntity.DATA_COUNT));
    }

    public PaintMixerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(SgMenuTypes.PAINT_MIXER.get(), containerId);
        this.container = container;
        this.data = data;

        addContainerInputSlots();
        addSlot(new SlotOutputOnly(this.container, this.container.getContainerSize() - 1, 126, 35));
        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        addDataSlots(this.data);
    }

    private void addContainerInputSlots() {
        int rowCount = 2;
        int rowSize = 4;
        int xOffset = 17;
        int yOffset = 26;

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < rowSize; ++col) {
                addSlot(new Slot(this.container, col + row * rowSize, xOffset + 18 * col, yOffset + 18 * row) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return PaintUtils.isPaintMixerInput(stack);
                    }
                });
            }
        }
    }

    @Override
    public boolean getWorkEnabled() {
        return this.data.get(1) != 0;
    }

    @Override
    public void setWorkEnabled(boolean value) {
        this.data.set(1, value ? 1 : 0);
    }

    public int getProgressArrowScale() {
        int progress = this.data.get(0);
        return progress != 0 ? progress * 24 / PaintMixerBlockEntity.WORK_TIME : 0;
    }

    public int getPaintColor() {
        return this.data.get(2);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            final int inventorySize = this.container.getContainerSize();
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;
            int outputSlot = inventorySize - 2;

            if (index == outputSlot) {
                // Move output to player
                if (!this.moveItemStackTo(stack1, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack1, stack);
            } else if (index >= inventorySize) {
                if (PaintUtils.isPaintMixerInput(stack1)) {
                    if (!this.moveItemStackTo(stack1, 0, outputSlot, false)) {
                        // Move from player or hotbar to input slots
                        return ItemStack.EMPTY;
                    }
                } else if (index < playerInventoryEnd) {
                    if (!this.moveItemStackTo(stack1, playerInventoryEnd, playerHotbarEnd, false)) {
                        // Move from player to hotbar
                        return ItemStack.EMPTY;
                    }
                } else if (index < playerHotbarEnd && !this.moveItemStackTo(stack1, inventorySize, playerInventoryEnd, false)) {
                    // Move from hotbar to player
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack1, inventorySize, playerHotbarEnd, false)) {
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

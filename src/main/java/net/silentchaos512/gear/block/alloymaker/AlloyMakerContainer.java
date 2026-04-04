package net.silentchaos512.gear.block.alloymaker;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.network.payload.client.AlloyMakerUpdatePayload;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;

import java.util.Collection;

public class AlloyMakerContainer extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData fields;

    public AlloyMakerContainer(MenuType<?> containerType, int id, Inventory playerInventory, FriendlyByteBuf buffer, Collection<IMaterialCategory> categories) {
        this(containerType, id, playerInventory, new SimpleContainer(buffer.readByte()), new SimpleContainerData(buffer.readByte()), categories);
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public AlloyMakerContainer(MenuType<?> containerType, int id, Inventory playerInventory, Container inventory, ContainerData fields, Collection<IMaterialCategory> categories) {
        super(containerType, id);
        this.inventory = inventory;
        this.fields = fields;

        addContainerInputSlots();
        addSlot(new SlotOutputOnly(this.inventory, this.inventory.getContainerSize() - 2, 126, 35));
        addSlot(new SlotOutputOnly(this.inventory, this.inventory.getContainerSize() - 1, 126, 60) {
            @Override
            public boolean mayPickup(Player playerIn) {
                return false;
            }
        });

        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        addDataSlots(this.fields);
    }

    private void addContainerInputSlots() {
        int inputSlotCount = this.inventory.getContainerSize() - 2;
        // Designed for 4, 6, or 8 input slots (4x1, 3x2, or 4x2)
        int rowCount = inputSlotCount > 4 ? 2 : 1;
        int rowSize = inputSlotCount == 6 ? 3 : 4;
        int xOffset = inputSlotCount == 6 ? 26 : 17;
        int yOffset = rowCount == 1 ? 35 : 26;

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < rowSize; ++col) {
                addSlot(new Slot(this.inventory, col + row * rowSize, xOffset + 18 * col, yOffset + 18 * row));
            }
        }
    }

    boolean getWorkEnabled() {
        return this.fields.get(1) != 0;
    }

    public void setWorkEnabled(boolean value) {
        this.fields.set(1, value ? 1 : 0);
    }

    void toggleWorkEnabled() {
        this.fields.set(1, this.fields.get(1) == 0 ? 1 : 0);
        ClientPacketDistributor.sendToServer(new AlloyMakerUpdatePayload(getWorkEnabled()));
    }

    public int getProgressArrowScale() {
        int progress = fields.get(0);
        return progress != 0 ? progress * 24 / AlloyMakerBlockEntity.WORK_TIME : 0;
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
            final int inventorySize = inventory.getContainerSize();
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
                if (isValidIngredient()) {
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

    private boolean isValidIngredient() {
        return true; // TODO
    }
}

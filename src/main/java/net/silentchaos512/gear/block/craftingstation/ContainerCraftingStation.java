package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.inventory.InventoryCraftingStation;

public class ContainerCraftingStation extends Container {
    InventoryCrafting craftMatrix;

    InventoryCraftResult craftResult;
    private final EntityPlayer player;

    private final TileCraftingStation tile;
    private final World world;
    public ContainerCraftingStation(InventoryPlayer playerInventory, World worldIn, BlockPos posIn, TileCraftingStation tile) {
        this.player = playerInventory.player;
        this.tile = tile;
        this.world = this.tile.getWorld();
        this.craftMatrix = new InventoryCraftingStation(this, tile, 3, 3);
        this.craftResult = new InventoryCraftResult();

        setupInventorySlots(playerInventory, this.tile);
    }

//    void setExtendedInventory(IInventory inventory) {
//        if (inventory == this.extendedInventory)
//            return;
//
//        this.extendedInventory = inventory;
//        setupInventorySlots(this.playerInventory, inventory);
//    }

    private void setupInventorySlots(InventoryPlayer playerInv, IInventory extendedInv) {
        inventorySlots.clear();
        inventoryItemStacks.clear();
        setupBasicSlots(playerInv);
        setupExtendedSlots(extendedInv);
    }

    private void setupBasicSlots(InventoryPlayer playerInventory) {
        int slotIndex = 0;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, slotIndex++, 146, 35));

        // Crafting grid
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                addSlotToContainer(new Slot(this.craftMatrix, slotIndex++, 8 + x * 18, 17 + y * 18));
            }
        }

        // Player backpack
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new Slot(playerInventory, slotIndex++, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player hotbar
        for (int x = 0; x < 9; ++x) {
            addSlotToContainer(new Slot(playerInventory, slotIndex++, 8 + x * 18, 142));
        }
    }

    private void setupExtendedSlots(IInventory inventory) {
        // Side inventory
        final int rowCount = (int) Math.ceil(TileCraftingStation.SIDE_INVENTORY_SIZE / 3.0);
        final int totalHeight = 44 + 18 * (rowCount - 2);

        for (int y = 0; y < rowCount; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = TileCraftingStation.SIDE_INVENTORY_START + x + y * 3;
                int xPos = x * 18 - 56;
                int yPos = y * 18 + 5 + (166 - totalHeight) / 2;
                addSlotToContainer(new Slot(tile, index, xPos, yPos));
            }
        }

        // Part slots
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 2; ++x) {
                // TODO: Need custom slot type?
                final int index = TileCraftingStation.GEAR_PARTS_START + x + y * 3;
                addSlotToContainer(new Slot(tile, index, 79 + x * 18, 17 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.isUsableByPlayer(playerIn);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        for (int i = 0; i < TileCraftingStation.CRAFTING_GRID_SIZE; ++i) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (!stack.isEmpty()) {
                tile.setInventorySlotContents(i + TileCraftingStation.CRAFTING_GRID_START, stack);
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (craftMatrix != null && craftResult != null) {
            slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
            // TODO: Apply parts to tool heads for fast tool crafting
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            final int playerStart = tile.getSizeInventory();
            final int hotbarStart = playerStart + 27;

            if (index == 0) { // Output slot
                itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);

                if (!this.mergeItemStack(itemstack1, playerStart, playerStart + 36, true)) { // To player and hotbar
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= playerStart && index < hotbarStart) { // Player
                if (!this.mergeItemStack(itemstack1, hotbarStart, hotbarStart + 9, false)) { // To hotbar
                    return ItemStack.EMPTY;
                }
            } else if (index >= hotbarStart && index < hotbarStart + 9) { // Hotbar
                if (!this.mergeItemStack(itemstack1, playerStart, playerStart + 27, false)) { // To player
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, playerStart, playerStart + 36, false)) { // To player and hotbar
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0) {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}

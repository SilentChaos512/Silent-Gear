package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.inventory.InventoryCraftingStation;

public class ContainerCraftingStation extends ContainerWorkbench {
    private final TileCraftingStation tile;
    private IInventory extendedInventory;
    private InventoryPlayer playerInventory;

    public ContainerCraftingStation(InventoryPlayer playerInventory, World worldIn, BlockPos posIn, TileCraftingStation tile) {

        super(playerInventory, worldIn, posIn);
        this.tile = tile;
        this.playerInventory = playerInventory;
        this.craftMatrix = new InventoryCraftingStation(this, 3, 3);
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
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        setupBasicSlots(playerInv);
        setupExtendedSlots(extendedInv);
    }

    private void setupBasicSlots(InventoryPlayer playerInventory) {
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    private void setupExtendedSlots(IInventory inventory) {
        final int invSize = inventory.getSizeInventory();
        int rowCount = (int) Math.ceil(invSize / 3.0);
        int totalHeight = 44 + 18 * (rowCount - 2);

        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = col + row * 3;
                if (index < invSize) {
                    int xPos = col * 18 - 56;
                    int yPos = row * 18 + 5 + (166 - totalHeight) / 2;
                    this.addSlotToContainer(new Slot(tile, index, xPos, yPos));
                } else {
                    return;
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        // TODO Auto-generated method stub
        return super.transferStackInSlot(playerIn, index);
    }
}

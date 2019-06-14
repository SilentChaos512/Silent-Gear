package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;

public class CraftingStationInventory extends CraftingInventory {
    private final CraftingStationTileEntity tileEntity;
    private final CraftingStationContainer container;

    public CraftingStationInventory(CraftingStationContainer containerIn, CraftingStationTileEntity tileEntityIn) {
        super(containerIn, 3, 3);
        this.container = containerIn;
        this.tileEntity = tileEntityIn;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return tileEntity.getStackInSlot(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return tileEntity.removeStackFromSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = tileEntity.decrStackSize(index, count);
        if (!stack.isEmpty()) {
            container.onCraftMatrixChanged(this);
        }
        return stack;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getSizeInventory(); ++i) {
            if (!tileEntity.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        tileEntity.setInventorySlotContents(index, stack);
        container.onCraftMatrixChanged(this);
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {
        for (int i = 0; i < getSizeInventory(); ++i) {
            helper.accountPlainStack(getStackInSlot(i));
        }
    }

    @Override
    public void markDirty() {
        tileEntity.markDirty();
    }
}

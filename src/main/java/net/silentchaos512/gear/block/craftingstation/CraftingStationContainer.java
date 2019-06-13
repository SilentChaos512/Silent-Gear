package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.world.World;
import net.silentchaos512.gear.init.ModContainers;

public class CraftingStationContainer extends RecipeBookContainer<CraftingStationTileEntity> {
    CraftingInventory craftMatrix;
    CraftResultInventory craftResult;

    private Slot outputSlot;
    private final PlayerEntity player;
    private final IInventory inventory;

    public CraftingStationContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(CraftingStationTileEntity.CRAFTING_GRID_SIZE + CraftingStationTileEntity.SIDE_INVENTORY_SIZE));
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public CraftingStationContainer(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(ModContainers.CRAFTING_STATION.type(), id);
        this.player = playerInventory.player;
        this.inventory = inventory;
        setupInventorySlots(playerInventory, inventory);

        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

//    void setExtendedInventory(IInventory inventory) {
//        if (inventory == this.extendedInventory)
//            return;
//
//        this.extendedInventory = inventory;
//        setupInventorySlots(this.playerInventory, inventory);
//    }

    //region Slots

    private void setupInventorySlots(PlayerInventory playerInv, IInventory extendedInv) {
        inventorySlots.clear();
        //inventoryItemStacks.clear();

        setupCraftingGrid();
        setupSideInventory();
        setupPlayerSlots(playerInv);

        // Output
        outputSlot = this.addSlot(new CraftingResultSlot(playerInv.player, this.craftMatrix, this.craftResult, inventory.getSizeInventory(), 124, 35));

        onCraftMatrixChanged(this.inventory);
    }

    private void setupCraftingGrid() {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                final int index = x + y * 3;
                addSlot(new Slot(this.craftMatrix, index, 30 + x * 18, 17 + y * 18));
            }
        }
    }

    private void setupPlayerSlots(PlayerInventory playerInventory) {
        // Player backpack
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                final int index = x + (y + 1) * 9;
                addSlot(new Slot(playerInventory, index, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player hotbar
        for (int x = 0; x < 9; ++x) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    private void setupSideInventory() {
        final int rowCount = (int) Math.ceil(CraftingStationTileEntity.SIDE_INVENTORY_SIZE / 3.0);
        final int totalHeight = 44 + 18 * (rowCount - 2);

        for (int y = 0; y < rowCount; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = CraftingStationTileEntity.SIDE_INVENTORY_START + x + y * 3;
                int xPos = x * 18 - 56;
                int yPos = y * 18 + 5 + (166 - totalHeight) / 2;
                addSlot(new Slot(inventory, index, xPos, yPos));
            }
        }
    }

    //endregion

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return inventory.isUsableByPlayer(playerIn);
    }

    @Override
    public Slot getSlot(int slotId) {
        return slotId == outputSlot.getSlotIndex() ? outputSlot : super.getSlot(slotId);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);

        for (int i = 0; i < CraftingStationTileEntity.CRAFTING_GRID_SIZE; ++i) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inventory.setInventorySlotContents(i + CraftingStationTileEntity.CRAFTING_GRID_START, stack);
            }
        }
        inventory.markDirty();
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (craftMatrix != null && craftResult != null) {
            slotChangedCraftingGrid(player.world, player, craftMatrix, craftResult);
            super.onCraftMatrixChanged(inventoryIn);
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            final int playerStart = inventory.getSizeInventory();
            final int hotbarStart = playerStart + 27;

            if (slot == outputSlot) { // Output slot
                itemstack1.getItem().onCreated(itemstack1, player.world, playerIn);

                if (!this.mergeItemStack(itemstack1, playerStart, playerStart + 36, true)) { // To player and hotbar
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= playerStart && index < hotbarStart) { // Player
                // To side inventory or hotbar?
                if (!this.mergeItemStack(itemstack1, CraftingStationTileEntity.SIDE_INVENTORY_START, CraftingStationTileEntity.SIDE_INVENTORY_START + CraftingStationTileEntity.SIDE_INVENTORY_SIZE, false)
                        && !this.mergeItemStack(itemstack1, hotbarStart, hotbarStart + 9, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= hotbarStart && index < hotbarStart + 9) { // Hotbar
                // To side inventory or player?
                if (!this.mergeItemStack(itemstack1, CraftingStationTileEntity.SIDE_INVENTORY_START, CraftingStationTileEntity.SIDE_INVENTORY_START + CraftingStationTileEntity.SIDE_INVENTORY_SIZE, false)
                        && !this.mergeItemStack(itemstack1, playerStart, playerStart + 27, false)) { // To player
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

//    @Override
    protected void slotChangedCraftingGrid(World worldIn, PlayerEntity playerIn, IInventory craftMatrixIn, CraftResultInventory craftResultIn) {
/*        if (player.world.isRemote) return;

        ServerPlayerEntity player = (ServerPlayerEntity) this.player;
        ItemStack itemstack = ItemStack.EMPTY;
        IRecipe irecipe = worldIn.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftMatrixIn, worldIn, net.minecraftforge.common.crafting.VanillaRecipeTypes.CRAFTING);

        if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || player.getRecipeBook().isUnlocked(irecipe))) {
            craftResult.setRecipeUsed(irecipe);
            itemstack = irecipe.getCraftingResult(craftMatrix);
        }

        final int index = outputSlot.getSlotIndex();
        craftResult.setInventorySlotContents(index, itemstack);
        player.connection.sendPacket(new SPacketSetSlot(this.windowId, index, itemstack));*/
    }

    @Override
    public void func_201771_a(RecipeItemHelper p_201771_1_) {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean matches(IRecipe<? super CraftingStationTileEntity> p_201769_1_) {
        return false;
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }
}

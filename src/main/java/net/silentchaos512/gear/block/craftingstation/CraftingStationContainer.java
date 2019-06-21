package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.silentchaos512.gear.init.ModContainers;

import java.util.Optional;

public class CraftingStationContainer extends RecipeBookContainer<CraftingStationInventory> {
    CraftingStationInventory craftMatrix;
    CraftResultInventory craftResult;
    private final CraftingStationTileEntity tileEntity;
    private final PlayerEntity player;
    private Slot outputSlot;

    public CraftingStationContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new CraftingStationTileEntity());
    }

    @SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
    public CraftingStationContainer(int id, PlayerInventory playerInventory, CraftingStationTileEntity tileEntityIn) {
        super(ModContainers.CRAFTING_STATION.type(), id);
        this.player = playerInventory.player;
        this.tileEntity = tileEntityIn;

        this.craftResult = new CraftResultInventory() {
            @Override
            public void markDirty() {
                CraftingStationContainer.this.tileEntity.markDirty();
            }
        };
        this.craftMatrix = new CraftingStationInventory(this, tileEntity);
        this.craftMatrix.openInventory(this.player);

        setupInventorySlots(playerInventory, this.tileEntity);

        onCraftMatrixChanged(craftMatrix);
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
        outputSlot = this.addSlot(new CraftingResultSlot(playerInv.player, this.craftMatrix, this.craftResult, tileEntity.getSizeInventory(), 124, 35));

        onCraftMatrixChanged(tileEntity);
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
                addSlot(new Slot(tileEntity, index, xPos, yPos));
            }
        }
    }

    //endregion

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        //noinspection OverlyLongLambda
        IWorldPosCallable.of(player.world, player.getPosition()).consume((world, pos) -> {
            if (!world.isRemote) {
                ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
                ItemStack stack = ItemStack.EMPTY;
                Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftMatrix, world);
                if (optional.isPresent()) {
                    ICraftingRecipe recipe = optional.get();
                    if (craftResult.canUseRecipe(world, playerMP, recipe)) {
                        stack = recipe.getCraftingResult(craftMatrix);
                    }
                }

                craftResult.setInventorySlotContents(outputSlot.slotNumber, stack);
                playerMP.connection.sendPacket(new SSetSlotPacket(windowId, outputSlot.slotNumber, stack));
            }
        });
    }

    @Override
    public void func_201771_a(RecipeItemHelper p_201771_1_) {
        craftMatrix.fillStackedContents(p_201771_1_);
    }

    @Override
    public void clear() {
        craftMatrix.clear();
        craftResult.clear();
    }

    @Override
    public boolean matches(IRecipe<? super CraftingStationInventory> recipe) {
        return recipe.matches(craftMatrix, player.world);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);

        for (int i = 0; i < CraftingStationTileEntity.CRAFTING_GRID_SIZE; ++i) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (!stack.isEmpty()) {
                tileEntity.setInventorySlotContents(i + CraftingStationTileEntity.CRAFTING_GRID_START, stack);
            }
        }
        tileEntity.markDirty();
    }

    @Override
    public int getOutputSlot() {
        return outputSlot.slotNumber;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public int getSize() {
        return tileEntity.getSizeInventory() + 1;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            final int playerStart = tileEntity.getSizeInventory();
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
}

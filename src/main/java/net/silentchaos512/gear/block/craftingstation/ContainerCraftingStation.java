package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.inventory.InventoryCraftingStation;
import net.silentchaos512.gear.inventory.SlotItemPart;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.gear.recipe.RecipeModularItem;

import java.util.HashSet;
import java.util.Set;

public class ContainerCraftingStation extends Container {
    InventoryCrafting craftMatrix;
    InventoryCraftResult craftResult;

    private Slot outputSlot;
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

    //region Slots

    private void setupInventorySlots(InventoryPlayer playerInv, IInventory extendedInv) {
        inventorySlots.clear();
        inventoryItemStacks.clear();

        setupCraftingGrid();
        setupPartSlots();
        setupSideInventory();
        setupPlayerSlots(playerInv);

        // Output
        // TODO: Need to extend SlotCrafting to remove proper ingredients?
        outputSlot = this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix,
                this.craftResult, tile.getSizeInventory(), 146, 35));

        onCraftMatrixChanged(this.tile);
    }

    private void setupCraftingGrid() {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                final int index = x + y * 3;
                addSlotToContainer(new Slot(this.craftMatrix, index, 8 + x * 18, 17 + y * 18));
            }
        }
    }

    private void setupPartSlots() {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 2; ++x) {
                final int index = TileCraftingStation.GEAR_PARTS_START + x + y * 2;
                addSlotToContainer(new SlotItemPart(this, tile, index, 79 + x * 18, 17 + y * 18));
            }
        }
    }

    private void setupPlayerSlots(InventoryPlayer playerInventory) {
        // Player backpack
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                final int index = x + (y + 1) * 9;
                addSlotToContainer(new Slot(playerInventory, index, 8 + x * 18, 84 + y * 18));
            }
        }

        // Player hotbar
        for (int x = 0; x < 9; ++x) {
            addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    private void setupSideInventory() {
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
    }

    //endregion

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.isUsableByPlayer(playerIn);
    }

    @Override
    public Slot getSlot(int slotId) {
        return slotId == outputSlot.getSlotIndex() ? outputSlot : super.getSlot(slotId);
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
        tile.markDirty();
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (craftMatrix != null && craftResult != null) {
            slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
            super.onCraftMatrixChanged(inventoryIn);
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

            if (slot == outputSlot) { // Output slot
                itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);

                if (!this.mergeItemStack(itemstack1, playerStart, playerStart + 36, true)) { // To player and hotbar
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= playerStart && index < hotbarStart) { // Player
                // To parts grid, side inventory, or hotbar?
                if (!this.mergeItemStack(itemstack1, TileCraftingStation.GEAR_PARTS_START, TileCraftingStation.GEAR_PARTS_START + TileCraftingStation.GEAR_PARTS_SIZE, false)
                        && !this.mergeItemStack(itemstack1, TileCraftingStation.SIDE_INVENTORY_START, TileCraftingStation.SIDE_INVENTORY_START + TileCraftingStation.SIDE_INVENTORY_SIZE, false)
                        && !this.mergeItemStack(itemstack1, hotbarStart, hotbarStart + 9, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= hotbarStart && index < hotbarStart + 9) { // Hotbar
                // To parts grid, side inventory, or player?
                if (!this.mergeItemStack(itemstack1, TileCraftingStation.GEAR_PARTS_START, TileCraftingStation.GEAR_PARTS_START + TileCraftingStation.GEAR_PARTS_SIZE, false)
                        && !this.mergeItemStack(itemstack1, TileCraftingStation.SIDE_INVENTORY_START, TileCraftingStation.SIDE_INVENTORY_START + TileCraftingStation.SIDE_INVENTORY_SIZE, false)
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

    @Override
    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMatrix, InventoryCraftResult craftResult) {
        if (!world.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
            ItemStack itemstack = ItemStack.EMPTY;
            IRecipe irecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

            if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
                craftResult.setRecipeUsed(irecipe);
                itemstack = irecipe.getCraftingResult(craftMatrix);

                // If output is a tool head, try using available parts to craft a complete tool
                if (itemstack.getItem() instanceof ToolHead) {
                    String toolClass = ToolHead.getToolClass(itemstack);
                    RecipeModularItem recipe = ModRecipes.gearCrafting.get(toolClass);
                    ICoreItem item = ModItems.gearClasses.get(toolClass);

                    if (recipe != null && item != null) {
                        NonNullList<ItemStack> partStackList = NonNullList.create();
                        partStackList.add(itemstack.copy());
                        Set<PartType> partTypesFound = new HashSet<>();

                        // Search for parts to apply
                        for (int i = TileCraftingStation.GEAR_PARTS_START; i < TileCraftingStation.GEAR_PARTS_START + TileCraftingStation.GEAR_PARTS_SIZE; ++i) {
                            final ItemStack partStack = getSlot(i).getStack();
                            final ItemPartData part = ItemPartData.fromStack(partStack);

                            if (part != null) {
                                final PartType type = part.getPart().getType();
                                final int requiredCount = item.getConfig().getCraftingPartCount(type);

                                // If required, must meet that number. Only one of each type, except misc upgrades.
                                final boolean isMiscUpgrade = type == PartType.MISC_UPGRADE;
                                if (requiredCount <= partStack.getCount() && (!partTypesFound.contains(type) || isMiscUpgrade)) {
                                    for (int j = 0; j < Math.max(requiredCount, 1); ++j) {
                                        ItemStack copy = partStack.copy();
                                        copy.setCount(1);
                                        partStackList.add(copy);
                                    }

                                    if (!isMiscUpgrade) {
                                        partTypesFound.add(type);
                                    }
                                }
                            }
                        }

                        if (!partTypesFound.isEmpty()) {
                            // Make the tool with extra parts
                            ItemStack result = recipe.getCraftingResult(partStackList);
                            if (!result.isEmpty()) {
                                itemstack = result;
                            }
                        }
                    }
                }
            }

            final int index = outputSlot.getSlotIndex();
            craftResult.setInventorySlotContents(index, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, index, itemstack));
        }
    }
}

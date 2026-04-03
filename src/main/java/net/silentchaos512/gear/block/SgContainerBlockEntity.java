package net.silentchaos512.gear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.Supplier;

public abstract class SgContainerBlockEntity extends BaseContainerBlockEntity {
    protected final ItemStackHandler items;
    private final int inventorySize;

    protected SgContainerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int inventorySize) {
        super(pType, pPos, pBlockState);
        this.inventorySize = inventorySize;
        this.items = createItemHandler();
    }

    /**
     * This constructor is provided for cases where additional parameters in the block entity's constructor are required
     * to create the item handler. In such cases, {@link #createItemHandler()} will not work.
     */
    protected SgContainerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int inventorySize, Supplier<ItemStackHandler> itemHandlerFactory) {
        super(pType, pPos, pBlockState);
        this.inventorySize = inventorySize;
        this.items = itemHandlerFactory.get();
    }

    /**
     * Creates an item handler for the block entity's inventory. This is called in the default constructor. If you
     * require information from fields in your block entity, use the
     * {@link #SgContainerBlockEntity(BlockEntityType, BlockPos, BlockState, int, Supplier)} constructor instead.
     *
     * @return The newly created item handler, which is stored in {@link #items}
     */
    public abstract ItemStackHandler createItemHandler();

    /**
     * Returns an item handler to be used for capabilities.
     *
     * @return The item handler
     */
    public IItemHandler getItemHandler() {
        return this.items;
    }

    @Deprecated
    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> result = NonNullList.withSize(this.items.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < this.items.getSlots(); ++i) {
            result.set(i, this.items.getStackInSlot(i));
        }
        return result;
    }

    @Deprecated
    @Override
    protected void setItems(NonNullList<ItemStack> pItems) {
        for (int i = 0; i < this.items.getSlots() && i < pItems.size(); ++i) {
            this.items.setStackInSlot(i, pItems.get(i));
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.items.getSlots(); ++i) {
            this.items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.getStackInSlot(pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemstack = this.items.getStackInSlot(pSlot);
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, pStack);
        this.items.setStackInSlot(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot < getContainerSize() - 1 && !flag) {
            this.setChanged();
        }
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        if (pSlot >= 0 && pSlot < this.items.getSlots() && !this.items.getStackInSlot(pSlot).isEmpty() && pAmount > 0) {
            var stackInSlot = this.items.getStackInSlot(pSlot);
            this.items.setStackInSlot(pSlot, stackInSlot.copyWithCount(stackInSlot.getCount() - pAmount));
            return stackInSlot.copyWithCount(pAmount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        if (pSlot >= 0 && pSlot < this.items.getSlots()) {
            ItemStack stack = this.items.getStackInSlot(pSlot);
            this.items.setStackInSlot(pSlot, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getContainerSize() {
        return this.items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.items.getSlots(); ++i) {
            var stack = this.items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canPlaceItem(int pSlot, ItemStack pStack) {
        return this.items.isItemValid(pSlot, pStack);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.deserializeNBT(registries, tag.getCompound("items"));
        // Loaded item list may not be the correct size, so we need to copy the deserialized list, set the correct size,
        // then copy the items back.
        var itemListWithCorrectSize = NonNullList.withSize(this.inventorySize, ItemStack.EMPTY);
        for (int i = 0; i < this.items.getSlots(); ++i) {
            itemListWithCorrectSize.set(i, this.items.getStackInSlot(i));
        }
        this.items.setSize(this.inventorySize);
        for (int i = 0; i < this.items.getSlots(); ++i) {
            this.items.setStackInSlot(i, itemListWithCorrectSize.get(i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("items", this.items.serializeNBT(registries));
    }
}

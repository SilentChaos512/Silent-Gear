package net.silentchaos512.gear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.function.Supplier;

public abstract class SgContainerBlockEntity extends BaseContainerBlockEntity {
    protected final NonNullList<ItemStack> items;
    private final int inventorySize;

    protected SgContainerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int inventorySize) {
        super(pType, pPos, pBlockState);
        this.inventorySize = inventorySize;
        this.items = createInternalItemList();
    }

    /**
     * This constructor is provided for cases where additional parameters in the block entity's constructor are required
     * to create the item handler. In such cases, {@link #createInternalItemList()} will not work.
     */
    protected SgContainerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int inventorySize, Supplier<NonNullList<ItemStack>> itemListFactory) {
        super(pType, pPos, pBlockState);
        this.inventorySize = inventorySize;
        this.items = itemListFactory.get();
    }

    /**
     * Creates an item handler for the block entity's inventory. This is called in the default constructor. If you
     * require information from fields in your block entity, use the
     * {@link #SgContainerBlockEntity(BlockEntityType, BlockPos, BlockState, int, Supplier)} constructor instead.
     *
     * @return The newly created item handler, which is stored in {@link #items}
     */
    public abstract NonNullList<ItemStack> createInternalItemList();

    @Override
    public abstract boolean canPlaceItem(int slot, ItemStack stack);

    public boolean canExtractItem(int slot) {
        return true;
    }

    /**
     * Returns an item handler to be used for capabilities.
     *
     * @return The item handler
     */
    public ResourceHandler<ItemResource> getItemHandler() {
        return new ItemStacksResourceHandler(this.items);
    }

    @Deprecated
    @Override
    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> result = NonNullList.withSize(this.items.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.items.size(); ++i) {
            result.set(i, this.items.get(i));
        }
        return result;
    }

    @Deprecated
    @Override
    protected void setItems(NonNullList<ItemStack> pItems) {
        for (int i = 0; i < this.items.size() && i < pItems.size(); ++i) {
            this.items.set(i, pItems.get(i));
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemstack = this.items.get(pSlot);
        boolean itemsIdentical = !pStack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, pStack);
        this.items.set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot < getContainerSize() - 1 && !itemsIdentical) {
            this.setChanged();
        }
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        if (pSlot >= 0 && pSlot < this.items.size() && !this.items.get(pSlot).isEmpty() && pAmount > 0) {
            var stackInSlot = this.items.get(pSlot);
            this.items.set(pSlot, stackInSlot.copyWithCount(stackInSlot.getCount() - pAmount));
            return stackInSlot.copyWithCount(pAmount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        if (pSlot >= 0 && pSlot < this.items.size()) {
            ItemStack stack = this.items.get(pSlot);
            this.items.set(pSlot, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }
}

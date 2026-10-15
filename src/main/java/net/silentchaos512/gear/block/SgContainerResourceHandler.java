package net.silentchaos512.gear.block;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

/**
 * Exposes an {@link SgContainerBlockEntity} to automation, honoring its
 * {@code canPlaceItem} / {@code canExtractItem} rules.
 * <p>
 * All writes use {@code setItem(slot, stack, true)} so that {@code setChanged()}
 * (which resets machine progress) is never called mid-transaction, which would
 * otherwise happen on every simulated insert/extract.
 */
public class SgContainerResourceHandler implements ResourceHandler<ItemResource> {
    private final SgContainerBlockEntity container;
    private final SlotJournal[] journals;

    public SgContainerResourceHandler(SgContainerBlockEntity container) {
        this.container = container;
        int size = container.getAutomationSlotCount();
        this.journals = new SlotJournal[size];
        for (int i = 0; i < size; ++i) {
            this.journals[i] = new SlotJournal(i);
        }
    }

    @Override
    public int size() {
        return this.journals.length;
    }

    @Override
    public ItemResource getResource(int index) {
        Objects.checkIndex(index, size());
        return ItemResource.of(this.container.getItem(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return this.container.getItem(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        Objects.checkIndex(index, size());
        // Per the ResourceHandler contract, a resource the slot would reject has no capacity
        if (!resource.isEmpty() && !isValid(index, resource)) {
            return 0;
        }
        return slotLimit(resource);
    }

    private int slotLimit(ItemResource resource) {
        int slotMax = this.container.getMaxStackSize();
        return resource.isEmpty() ? slotMax : Math.min(slotMax, resource.getMaxStackSize());
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        Objects.checkIndex(index, size());
        return !resource.isEmpty() && this.container.canPlaceItem(index, resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        Objects.checkIndex(index, size());

        if (amount == 0) {
            return 0;
        }

        // Cheap checks first: isValid can scan every recipe (AlloyMaker#acceptsInput)
        ItemStack current = this.container.getItem(index);
        if (!current.isEmpty() && !resource.matches(current)) {
            return 0;
        }

        // Computed inline rather than via getCapacityAsLong, which would call isValid a second time
        int space = slotLimit(resource) - current.getCount();
        int toInsert = Math.min(amount, space);
        if (toInsert <= 0 || !isValid(index, resource)) {
            return 0;
        }

        this.journals[index].updateSnapshots(transaction);
        // Never mutate the live stack: the journal's snapshot is a separate copy, and
        // replacing the stack keeps aliasing out of the picture.
        this.container.setItem(index, resource.toStack(current.getCount() + toInsert), true);
        return toInsert;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        Objects.checkIndex(index, size());

        if (amount == 0 || !this.container.canExtractItem(index)) {
            return 0;
        }

        ItemStack current = this.container.getItem(index);
        if (current.isEmpty() || !resource.matches(current)) {
            return 0;
        }

        int toExtract = Math.min(amount, current.getCount());
        this.journals[index].updateSnapshots(transaction);
        int remaining = current.getCount() - toExtract;
        this.container.setItem(index, remaining <= 0 ? ItemStack.EMPTY : current.copyWithCount(remaining), true);
        return toExtract;
    }

    private final class SlotJournal extends SnapshotJournal<ItemStack> {
        private final int slot;

        SlotJournal(int slot) {
            this.slot = slot;
        }

        @Override
        protected ItemStack createSnapshot() {
            return SgContainerResourceHandler.this.container.getItem(this.slot).copy();
        }

        @Override
        protected void revertToSnapshot(ItemStack snapshot) {
            SgContainerResourceHandler.this.container.setItem(this.slot, snapshot, true);
        }

        @Override
        protected void onRootCommit(ItemStack originalState) {
            SgContainerResourceHandler.this.container.onAutomationCommit(this.slot, originalState);
        }
    }
}

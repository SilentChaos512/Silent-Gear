package net.silentchaos512.gear.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Item handler for items that store other items in an {@link ItemContainerContents} component. Replaces NeoForge's
 * removed {@code ComponentItemHandler}. Also implements {@link IndexModifier} so it can back
 * {@link net.neoforged.neoforge.transfer.item.ResourceHandlerSlot}s in menus.
 * @since 4.4.0
 */
public class ContainerItemHandler extends ItemAccessItemHandler implements IndexModifier<ItemResource> {
    public ContainerItemHandler(ItemStack stack, DataComponentType<ItemContainerContents> component, int size) {
        super(ItemAccess.forStack(stack), component, size);
    }

    public ItemStack getStackInSlot(int index) {
        return ItemUtil.getStack(this, index);
    }

    /**
     * Inserts a stack into the given slot.
     *
     * @return The part of the stack that did not fit
     */
    public ItemStack insertItem(int index, ItemStack stack) {
        return ItemUtil.insertItemReturnRemaining(this, index, stack, false, null);
    }

    @Override
    public void set(int index, ItemResource resource, int amount) {
        ItemResource updated = update(itemAccess.getResource(), index, resource, amount);
        if (updated == null || updated.isEmpty()) {
            return;
        }
        try (var transaction = Transaction.openRoot()) {
            int accessAmount = itemAccess.getAmount();
            if (itemAccess.exchange(updated, accessAmount, transaction) == accessAmount) {
                transaction.commit();
            }
        }
    }
}

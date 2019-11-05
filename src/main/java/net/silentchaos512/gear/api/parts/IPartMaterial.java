package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.silentchaos512.lib.event.ClientTicks;

import java.util.function.Predicate;

public interface IPartMaterial extends Predicate<ItemStack> {
    /**
     * Gets the ingredient used for crafting (ingot, gem, etc.)
     *
     * @return The crafting ingredient
     */
    Ingredient getNormal();

    /**
     * Gets the "small" ingredient (nuggets, etc.) Currently not used for anything.
     *
     * @return The small crafting ingredient
     */
    Ingredient getSmall();

    /**
     * Get an {@code ItemStack} which matches the normal ingredient. The {@code ticks} parameter can
     * be used to cycle between possible matches.
     *
     * @param ticks Used to index into matching stacks. If on the client, {@link
     *              ClientTicks#totalTicks()} can be used. Zero will consistently return the first
     *              item in the matching stacks array.
     * @return An item matching the normal ingredient, or {@link ItemStack#EMPTY} if there are none
     */
    default ItemStack getDisplayItem(int ticks) {
        ItemStack[] stacks = getNormal().getMatchingStacks();
        if (stacks.length == 0) return ItemStack.EMPTY;
        return stacks[(ticks / 20) % stacks.length];
    }

    default void write(PacketBuffer buffer) {
        getNormal().write(buffer);
        getSmall().write(buffer);
    }
}

package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.ICoreItem;

/**
 * Represents an upgrade part, which is applied to gear after it has been crafted.
 */
public interface IUpgradePart {
    /**
     * Check if the part can be applied to the gear type. This is <em>not</em> responsible for
     * checking for part conflicts, it only checks the item type.
     *
     * @param gearItem The item
     * @return True if {@code gearItem} can accept the upgrade, false otherwise
     */
    default boolean isValidFor(ICoreItem gearItem) {
        return true;
    }

    /**
     * Determine if the part should replace another part occupying the same {@link IPartPosition}.
     *
     * @return True if existing parts in the same position should be replaced
     */
    default boolean replacesExisting() {
        return false;
    }

    /**
     * Called when the upgrade part has been added to a gear item. This could be used to attach
     * additional data to the tool, since {@link net.silentchaos512.gear.parts.PartData} is not
     * always reliable for tracking extra data.
     *
     * @param gear The gear item
     * @param part The upgrade item that was used in crafting
     */
    default void onAddToGear(ItemStack gear, ItemStack part) {
    }
}

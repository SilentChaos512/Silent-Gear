package net.silentchaos512.gear.api.parts;

import net.silentchaos512.gear.api.item.ICoreItem;

/**
 * Represents an upgrade part, which is applied to gear after it has been crafted.
 */
public interface IUpgradePart {
    /**
     * Check if the part can be applied to the gear type. This is <em>not</em> responsible for
     * checking for part conflicts, it only checks the item type.
     *
     * @return True if {@code gearItem} can accept the upgrade, false otherwise
     */
    default boolean isValidFor(ICoreItem gearItem) {
        return true;
    }

    default boolean replacesExisting() {
        return false;
    }
}

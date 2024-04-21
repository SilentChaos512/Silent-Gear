package net.silentchaos512.gear.api.traits;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record TraitActionContext(@Nullable Player player, int traitLevel, ItemStack gear) {
    /**
     * Gets the player using the gear, if there is one.
     *
     * @return The user of the gear item, or null if not available
     */
    @Override
    @Nullable
    public Player player() {
        return player;
    }

    /**
     * Gets the level of the trait
     *
     * @return The trait level
     */
    @Override
    public int traitLevel() {
        return traitLevel;
    }

    /**
     * Gets the gear item the trait is on
     *
     * @return The gear item
     */
    @Override
    public ItemStack gear() {
        return gear;
    }
}

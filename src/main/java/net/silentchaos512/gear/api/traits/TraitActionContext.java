package net.silentchaos512.gear.api.traits;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class TraitActionContext {
    @Nullable private final Player player;
    private final int traitLevel;
    private final ItemStack gear;

    public TraitActionContext(@Nullable Player player, int traitLevel, ItemStack gear) {
        this.player = player;
        this.traitLevel = traitLevel;
        this.gear = gear;
    }

    /**
     * Gets the player using the gear, if there is one.
     *
     * @return The user of the gear item, or null if not available
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the level of the trait
     *
     * @return The trait level
     */
    public int getTraitLevel() {
        return traitLevel;
    }

    /**
     * Gets the gear item the trait is on
     *
     * @return The gear item
     */
    public ItemStack getGear() {
        return gear;
    }
}

package net.silentchaos512.gear.api.traits;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.trait.Trait;

import javax.annotation.Nullable;

@Deprecated
public record TraitActionContext(
        @Nullable Player player,
        Trait trait,
        int traitLevel,
        ItemStack gear
) {
    public TraitActionContext(@Nullable Player player, TraitInstance instance, ItemStack gear) {
        this(player, instance.getTrait(), instance.getLevel(), gear);
    }
}

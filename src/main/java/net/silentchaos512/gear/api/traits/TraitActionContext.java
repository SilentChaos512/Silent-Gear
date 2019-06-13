package net.silentchaos512.gear.api.traits;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

@Getter
public class TraitActionContext {
    @Nullable private final PlayerEntity player;
    private final int traitLevel;
    private final ItemStack gear;

    public TraitActionContext(@Nullable PlayerEntity player, int traitLevel, ItemStack gear) {
        this.player = player;
        this.traitLevel = traitLevel;
        this.gear = gear;
    }
}

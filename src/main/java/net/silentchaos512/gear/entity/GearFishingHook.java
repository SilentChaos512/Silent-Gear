package net.silentchaos512.gear.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

public class GearFishingHook extends FishingHook {
    public GearFishingHook(EntityType<? extends FishingHook> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GearFishingHook(Player pPlayer, Level pLevel, int pLuck, int pLureSpeed) {
        super(pPlayer, pLevel, pLuck, pLureSpeed);
    }
}

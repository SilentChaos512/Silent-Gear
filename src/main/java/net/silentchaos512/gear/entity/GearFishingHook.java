package net.silentchaos512.gear.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class GearFishingHook extends FishingHook implements IEntityWithComplexSpawn {
    public GearFishingHook(EntityType<? extends FishingHook> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GearFishingHook(Player pPlayer, Level pLevel, int pLuck, int pLureSpeed) {
        super(pPlayer, pLevel, pLuck, pLureSpeed);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
    }
}

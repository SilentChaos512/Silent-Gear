package net.silentchaos512.gear.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.item.gear.GearFishingRodItem;

public class GearFishingHook extends FishingHook implements IEntityWithComplexSpawn {
    public GearFishingHook(EntityType<? extends FishingHook> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GearFishingHook(Player pPlayer, Level pLevel, int pLuck, int pLureSpeed) {
        super(pPlayer, pLevel, pLuck, pLureSpeed);
    }

    @Override
    private boolean shouldStopFishing(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean isMainHand = mainHand.getItem() instanceof GearFishingRodItem;
        boolean isOffHand = offHand.getItem() instanceof GearFishingRodItem;
        if (!player.isRemoved() && player.isAlive() && (isMainHand || isOffHand) && !(this.distanceToSqr(player) > 1024.0)) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
    }
}

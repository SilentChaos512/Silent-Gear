package net.silentchaos512.gear.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.PlayMessages;
import net.silentchaos512.gear.init.SgEntities;
import net.silentchaos512.gear.item.gear.GearFishingRodItem;

public class GearFishingHook extends FishingHook implements IEntityAdditionalSpawnData {
    public GearFishingHook(EntityType<? extends GearFishingHook> type, Level level) {
        super(type, level);
    }

    public GearFishingHook(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
    }

    public GearFishingHook(PlayMessages.SpawnEntity message, Level level) {
        this(SgEntities.FISHING_HOOK.get(), level);
    }

    @Override
    protected boolean shouldStopFishing(Player player) {
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

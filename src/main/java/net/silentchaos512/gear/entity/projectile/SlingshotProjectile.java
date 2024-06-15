package net.silentchaos512.gear.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.SgItems;

public class SlingshotProjectile extends AbstractArrow {
    public SlingshotProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel, ItemStack pPickupItemStack) {
        super(pEntityType, pLevel, pPickupItemStack);
    }

    public SlingshotProjectile(EntityType<? extends AbstractArrow> pEntityType, double pX, double pY, double pZ, Level pLevel, ItemStack pPickupItemStack) {
        super(pEntityType, pX, pY, pZ, pLevel, pPickupItemStack);
    }

    public SlingshotProjectile(EntityType<? extends AbstractArrow> pEntityType, LivingEntity pOwner, Level pLevel, ItemStack pPickupItemStack) {
        super(pEntityType, pOwner, pLevel, pPickupItemStack);
    }

    public SlingshotProjectile(EntityType<SlingshotProjectile> type, Level level) {
        super(type, level, SgItems.PEBBLE.toStack());
    }

    @Override
    protected ItemStack getPickupItem() {
        return SgItems.PEBBLE.toStack();
    }

    public ItemStack getItem() {
        return getPickupItem();
    }

    @Override
    public void tick() {
        super.tick();
        if (inGround) {
            this.discard();
        }
    }
}

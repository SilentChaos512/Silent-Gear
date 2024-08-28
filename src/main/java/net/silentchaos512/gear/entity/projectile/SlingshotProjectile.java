package net.silentchaos512.gear.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.SgEntities;

import javax.annotation.Nullable;

public class SlingshotProjectile extends AbstractArrow {
    public SlingshotProjectile(LivingEntity pOwner, Level pLevel, ItemStack pPickupItemStack, @Nullable ItemStack firedWeapon) {
        super(SgEntities.SLINGSHOT_PROJECTILE.get(), pOwner, pLevel, pPickupItemStack, firedWeapon);
    }

    public SlingshotProjectile(EntityType<SlingshotProjectile> slingshotProjectileEntityType, Level level) {
        super(slingshotProjectileEntityType, level);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();
        if (inGround) {
            this.discard();
        }
    }
}

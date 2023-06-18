package net.silentchaos512.gear.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.setup.SgItems;

public class SlingshotProjectile extends AbstractArrow {
    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, double x, double y, double z, Level worldIn) {
        super(type, x, y, z, worldIn);
    }

    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, LivingEntity entityIn, Level worldIn) {
        super(type, entityIn, worldIn);
    }

    public SlingshotProjectile(LivingEntity entityIn, Level worldIn) {
        super(SgEntities.SLINGSHOT_PROJECTILE.get(), entityIn, worldIn);
    }

    public SlingshotProjectile(PlayMessages.SpawnEntity message, Level world) {
        super(SgEntities.SLINGSHOT_PROJECTILE.get(), world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(SgItems.PEBBLE);
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

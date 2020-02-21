package net.silentchaos512.gear.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.init.ModEntities;
import net.silentchaos512.gear.init.ModItems;

// FIXME
public class SlingshotProjectile extends AbstractArrowEntity {
    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, World worldIn) {
        super(type, worldIn);
    }

    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, double x, double y, double z, World worldIn) {
        super(type, x, y, z, worldIn);
    }

    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, LivingEntity entityIn, World worldIn) {
        super(type, entityIn, worldIn);
    }

    public SlingshotProjectile(LivingEntity entityIn, World worldIn) {
        super(ModEntities.SLINGSHOT_PROJECTILE.get(), entityIn, worldIn);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ModItems.pebble);
    }

    public ItemStack getItem() {
        return getArrowStack();
    }
}

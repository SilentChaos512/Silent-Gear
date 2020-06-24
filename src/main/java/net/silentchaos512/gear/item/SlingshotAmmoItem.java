package net.silentchaos512.gear.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;

public class SlingshotAmmoItem extends ArrowItem implements ISlingshotAmmo {
    public SlingshotAmmoItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {
        return new SlingshotProjectile(shooter, worldIn);
    }
}

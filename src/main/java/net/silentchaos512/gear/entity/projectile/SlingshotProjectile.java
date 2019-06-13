package net.silentchaos512.gear.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.init.ModItems;

// FIXME
public class SlingshotProjectile extends AbstractArrowEntity implements IProjectile {
    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> type, World p_i48546_2_) {
        super(type, p_i48546_2_);
    }

    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> p_i48547_1_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
        super(p_i48547_1_, p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public SlingshotProjectile(EntityType<? extends SlingshotProjectile> p_i48548_1_, LivingEntity p_i48548_2_, World p_i48548_3_) {
        super(p_i48548_1_, p_i48548_2_, p_i48548_3_);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ModItems.pebble);
    }
}

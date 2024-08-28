package net.silentchaos512.gear.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;

public class GearArrowEntity extends Arrow {
    private ItemStack arrowStack = ItemStack.EMPTY;

    public GearArrowEntity(EntityType<? extends Arrow> type, Level worldIn) {
        super(type, worldIn);
    }

    public GearArrowEntity(Level worldIn, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedWeapon) {
        super(worldIn, x, y, z, pickupItemStack, firedWeapon);
    }

    public GearArrowEntity(Level worldIn, LivingEntity shooter, ItemStack pickupItemStack, @Nullable ItemStack firedWeapon) {
        super(worldIn, shooter, pickupItemStack, firedWeapon);
    }

    public void setArrowStack(ItemStack stack) {
        this.arrowStack = stack.copyWithCount(1);
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float speedMulti = GearData.getProperties(arrowStack).getNumber(GearProperties.PROJECTILE_SPEED);
        float accuracy = GearData.getProperties(arrowStack).getNumber(GearProperties.PROJECTILE_ACCURACY);
        super.shootFromRotation(shooter, x, y, z, velocity * speedMulti, accuracy > 0f ? inaccuracy / accuracy : inaccuracy);
    }
}

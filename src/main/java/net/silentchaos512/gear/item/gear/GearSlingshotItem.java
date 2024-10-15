package net.silentchaos512.gear.item.gear;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.item.SlingshotAmmoItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class GearSlingshotItem extends GearBowItem {
    /**
     * Extra damage added by "power" enchantment. Bows are 0.5.
     */
    private static final float POWER_SCALE = 0.35f;

    public GearSlingshotItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public float getArrowVelocity(ItemStack stack, int charge) {
        return super.getArrowVelocity(stack, charge);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof ISlingshotAmmo;
    }

    @Override
    protected Projectile createProjectile(Level pLevel, LivingEntity pShooter, ItemStack pWeapon, ItemStack pAmmo, boolean pIsCrit) {
        SlingshotAmmoItem item = pAmmo.getItem() instanceof SlingshotAmmoItem slingshotAmmoItem ? slingshotAmmoItem : SgItems.PEBBLE.get();
        AbstractArrow projectile = item.createArrow(pLevel, pAmmo, pShooter, pWeapon);
        if (pIsCrit) {
            projectile.setCritArrow(true);
            var rangedDamage = GearData.getProperties(pWeapon).getNumber(GearProperties.RANGED_DAMAGE);
            projectile.setBaseDamage(projectile.getBaseDamage() - 1 + rangedDamage);
        }

        return customArrow(projectile, pAmmo, pWeapon);
    }
}

package net.silentchaos512.gear.item.gear;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import net.silentchaos512.utils.MathUtils;

import java.util.Optional;

public class CoreSlingshot extends CoreBow {
    /**
     * Extra damage added by "power" enchantment. Bows are 0.5.
     */
    private static final float POWER_SCALE = 0.35f;

    @Override
    public GearType getGearType() {
        return GearType.SLINGSHOT;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == CommonItemStats.RANGED_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(0));
        if (stat == CommonItemStats.RANGED_SPEED)
            return Optional.of(StatInstance.makeBaseMod(1.5f));
        if (stat == CommonItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(2));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == CommonItemStats.ENCHANTABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.65f));
        return Optional.empty();
    }

    @Override
    public float getArrowVelocity(ItemStack stack, int charge) {
        return super.getArrowVelocity(stack, charge);
    }

    @Override
    public float getArrowDamage(ItemStack stack) {
        return super.getArrowDamage(stack);
    }

    @Override
    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ISlingshotAmmo
                && ((ISlingshotAmmo) stack.getItem()).isAmmo(stack);
    }

    @Override
    protected boolean isInfiniteAmmo(ItemStack bow, ItemStack ammo, EntityPlayer player) {
        // TODO
        return super.isInfiniteAmmo(bow, ammo, player);
    }

    @Override
    protected void fireProjectile(ItemStack stack, World worldIn, EntityPlayer player, ItemStack ammo, float velocity, boolean hasInfiniteAmmo) {
        SlingshotProjectile entity = new SlingshotProjectile(player, worldIn, ammo);
        entity.shoot(player, player.rotationPitch, player.rotationYaw, velocity * 3.0F, 1.0F);

        if (MathUtils.doublesEqual(velocity, 1.0F)) {
            entity.setIsCritical(true);
        }

        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        float powerBoost = power > 0 ? power * POWER_SCALE + POWER_SCALE : 0.0f;
        float damageBoost = getArrowDamage(stack);
        entity.setDamage(damageBoost + powerBoost);

        int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        if (punchLevel > 0) {
            entity.setKnockbackStrength(punchLevel / 2);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
            entity.setFire(100);
        }

        stack.damageItem(1, player);

        worldIn.spawnEntity(entity);
    }
}

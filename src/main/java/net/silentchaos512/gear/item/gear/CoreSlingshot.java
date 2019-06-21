package net.silentchaos512.gear.item.gear;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

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
        if (stat == ItemStats.RANGED_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(0));
        if (stat == ItemStats.RANGED_SPEED)
            return Optional.of(StatInstance.makeBaseMod(1.5f));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(2));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == ItemStats.ENCHANTABILITY)
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

/*    @Override
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
    }*/
}

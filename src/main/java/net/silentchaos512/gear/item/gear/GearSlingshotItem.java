package net.silentchaos512.gear.item.gear;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.item.SlingshotAmmoItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.utils.MathUtils;

import java.util.function.Predicate;

public class GearSlingshotItem extends GearBowItem {
    /**
     * Extra damage added by "power" enchantment. Bows are 0.5.
     */
    private static final float POWER_SCALE = 0.35f;

    @Override
    public GearType getGearType() {
        return GearType.SLINGSHOT;
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
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof ISlingshotAmmo;
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

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isClientSide) {
//            ToolModel.bowPull.remove(GearData.getUUID(stack));
        }

        if (entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            boolean infiniteAmmo = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack ammoItem = player.getProjectile(stack);

            int i = this.getUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, player, i, !ammoItem.isEmpty() || infiniteAmmo);
            if (i < 0) return;

            if (!ammoItem.isEmpty() || infiniteAmmo) {
                if (ammoItem.isEmpty()) {
                    ammoItem = new ItemStack(SgItems.PEBBLE);
                }

                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (ammoItem.getItem() instanceof SlingshotAmmoItem && ((SlingshotAmmoItem) ammoItem.getItem()).isInfinite(ammoItem, stack, player));
                    if (!worldIn.isClientSide) {
                        SlingshotAmmoItem slingshotAmmoItem = (SlingshotAmmoItem) (ammoItem.getItem() instanceof SlingshotAmmoItem ? ammoItem.getItem() : SgItems.PEBBLE.get());
                        AbstractArrow shot = slingshotAmmoItem.createArrow(worldIn, ammoItem, player);
                        shot.setBaseDamage(shot.getBaseDamage() + GearData.getStat(stack, ItemStats.RANGED_DAMAGE));
                        shot.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (MathUtils.floatsEqual(f, 1.0f)) {
                            shot.setCritArrow(true);
                        }

                        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (powerLevel > 0) {
                            shot.setBaseDamage(shot.getBaseDamage() + (double) powerLevel * POWER_SCALE + POWER_SCALE);
                        }

                        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (punchLevel > 0) {
                            shot.setKnockback(punchLevel);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            shot.setSecondsOnFire(100);
                        }

                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(p.getUsedItemHand()));
                        if (flag1 || player.getAbilities().instabuild && (ammoItem.getItem() == Items.SPECTRAL_ARROW || ammoItem.getItem() == Items.TIPPED_ARROW)) {
                            shot.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        EntityHelper.spawnWithClientPacket(worldIn, shot);
                    }

                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        ammoItem.shrink(1);
                        if (ammoItem.isEmpty()) {
                            player.getInventory().removeItem(ammoItem);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }
}

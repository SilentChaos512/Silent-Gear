package net.silentchaos512.gear.item.gear;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.SlingshotAmmoItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.utils.MathUtils;

import java.util.Optional;
import java.util.function.Predicate;

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
        if (stat == ItemStats.RANGED_DAMAGE)
            return Optional.of(StatInstance.makeGearMod(-0.75f));
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
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
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
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isRemote) {
//            ToolModel.bowPull.remove(GearData.getUUID(stack));
        }

        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            boolean infiniteAmmo = player.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack ammoItem = player.findAmmo(stack);

            int i = this.getUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, player, i, !ammoItem.isEmpty() || infiniteAmmo);
            if (i < 0) return;

            if (!ammoItem.isEmpty() || infiniteAmmo) {
                if (ammoItem.isEmpty()) {
                    ammoItem = new ItemStack(ModItems.PEBBLE);
                }

                float f = getArrowVelocity(i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.abilities.isCreativeMode || (ammoItem.getItem() instanceof SlingshotAmmoItem && ((SlingshotAmmoItem) ammoItem.getItem()).isInfinite(ammoItem, stack, player));
                    if (!worldIn.isRemote) {
                        SlingshotAmmoItem slingshotAmmoItem = (SlingshotAmmoItem) (ammoItem.getItem() instanceof SlingshotAmmoItem ? ammoItem.getItem() : ModItems.PEBBLE);
                        AbstractArrowEntity shot = slingshotAmmoItem.createArrow(worldIn, ammoItem, player);
                        shot.setDamage(shot.getDamage() + GearData.getStat(stack, ItemStats.RANGED_DAMAGE));
                        shot.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                        if (MathUtils.floatsEqual(f, 1.0f)) {
                            shot.setIsCritical(true);
                        }

                        int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (powerLevel > 0) {
                            shot.setDamage(shot.getDamage() + (double) powerLevel * POWER_SCALE + POWER_SCALE);
                        }

                        int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (punchLevel > 0) {
                            shot.setKnockbackStrength(punchLevel);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            shot.setFire(100);
                        }

                        stack.damageItem(1, player, (p) -> p.sendBreakAnimation(p.getActiveHand()));
                        if (flag1 || player.abilities.isCreativeMode && (ammoItem.getItem() == Items.SPECTRAL_ARROW || ammoItem.getItem() == Items.TIPPED_ARROW)) {
                            shot.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }

                        EntityHelper.spawnWithClientPacket(worldIn, shot);
                    }

                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.abilities.isCreativeMode) {
                        ammoItem.shrink(1);
                        if (ammoItem.isEmpty()) {
                            player.inventory.deleteStack(ammoItem);
                        }
                    }

                    player.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }
}

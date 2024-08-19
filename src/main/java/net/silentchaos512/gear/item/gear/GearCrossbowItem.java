package net.silentchaos512.gear.item.gear;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreRangedWeapon;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearCrossbowItem extends CrossbowItem implements ICoreRangedWeapon {
    private static final int MIN_CHARGE_TIME = 5;
    private static final int MAX_CHARGE_TIME = 50;

    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    private final Supplier<GearType> gearType;

    public GearCrossbowItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    //region Crossbow stuff


    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (GearHelper.isBroken(itemstack)) {
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }
        if (isCharged(itemstack)) {
            fireProjectiles(worldIn, playerIn, handIn, itemstack, getShootingPower(itemstack), 1.0F);
            setCharged(itemstack, false);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        } else if (!playerIn.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                playerIn.startUsingItem(handIn);
            }

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        int i = this.getUseDuration(stack) - timeLeft;
        float f = getCharge(i, stack);
        if (f >= 1.0F && !isCharged(stack) && hasAmmo(entityLiving, stack)) {
            setCharged(stack, true);
            SoundSource soundcategory = entityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (worldIn.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    private static boolean hasAmmo(LivingEntity shooter, ItemStack stack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack);
        int j = i == 0 ? 1 : 3;
        boolean flag = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
        ItemStack itemstack = shooter.getProjectile(stack);
        ItemStack itemstack1 = itemstack.copy();

        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack = itemstack1.copy();
            }

            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }

            if (!loadProjectile(shooter, stack, itemstack, k > 0, flag)) {
                return false;
            }
        }

        return true;
    }

    private static boolean loadProjectile(LivingEntity entityIn, ItemStack crossbow, ItemStack ammo, boolean p_220023_3_, boolean p_220023_4_) {
        if (isAmmoInvalid(ammo)) {
            return false;
        } else {
            boolean flag = p_220023_4_ && ammo.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !p_220023_4_ && !p_220023_3_) {
                itemstack = splitOneAmmo(ammo);
                if (ammo.isEmpty() && entityIn instanceof Player) {
                    ((Player) entityIn).getInventory().removeItem(ammo);
                }
            } else {
                itemstack = ammo.copy();
            }

            addChargedProjectile(crossbow, itemstack);
            return true;
        }
    }

    private static boolean isAmmoInvalid(ItemStack ammo) {
        // Temp fix for https://github.com/SilentChaos512/Silent-Gear/issues/270
        return ammo.isEmpty() || (ammo.getItem() instanceof GearArrowItem && GearHelper.isBroken(ammo));
    }

    private static ItemStack splitOneAmmo(ItemStack ammo) {
        // Temp fix for https://github.com/SilentChaos512/Silent-Gear/issues/270
        if (ammo.getItem() instanceof GearArrowItem) {
            ItemStack copy = ammo.copy();
            ammo.setDamageValue(ammo.getDamageValue() + 1);
            copy.setDamageValue(copy.getMaxDamage() - 1);
            return copy;
        }
        return ammo.split(1);
    }

    private static void addChargedProjectile(ItemStack crossbow, ItemStack projectile) {
        CompoundTag compoundnbt = crossbow.getOrCreateTag();
        ListTag listnbt;
        if (compoundnbt.contains("ChargedProjectiles", 9)) {
            listnbt = compoundnbt.getList("ChargedProjectiles", 10);
        } else {
            listnbt = new ListTag();
        }

        CompoundTag compoundnbt1 = new CompoundTag();
        projectile.save(compoundnbt1);
        listnbt.add(compoundnbt1);
        compoundnbt.put("ChargedProjectiles", listnbt);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack p_220018_0_) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag compoundnbt = p_220018_0_.getTag();
        if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9)) {
            ListTag listnbt = compoundnbt.getList("ChargedProjectiles", 10);
            //noinspection ConstantConditions
            if (listnbt != null) {
                for (int i = 0; i < listnbt.size(); ++i) {
                    CompoundTag compoundnbt1 = listnbt.getCompound(i);
                    list.add(ItemStack.of(compoundnbt1));
                }
            }
        }

        return list;
    }

    private static void clearProjectiles(ItemStack p_220027_0_) {
        CompoundTag compoundnbt = p_220027_0_.getTag();
        if (compoundnbt != null) {
            ListTag listnbt = compoundnbt.getList("ChargedProjectiles", 9);
            listnbt.clear();
            compoundnbt.put("ChargedProjectiles", listnbt);
        }

    }

    @Override
    public int getUseDuration(ItemStack stack) {
        // For vanilla crossbows, this is getChargeTime + 3
        return 133700;
    }

    private SoundEvent getStartSound(int p_220025_1_) {
        switch(p_220025_1_) {
            case 1:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            case 2:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            case 3:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            default:
                return SoundEvents.CROSSBOW_LOADING_START;
        }
    }

    private static float getCharge(int useTime, ItemStack stack) {
        float f = (float) useTime / getChargeTime(stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    private static void shootProjectile(Level world, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, ItemStack projectile, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
        if (!world.isClientSide) {
            boolean flag = projectile.getItem() == Items.FIREWORK_ROCKET;
            Projectile iprojectile;
            if (flag) {
                iprojectile = new FireworkRocketEntity(world, projectile, shooter.getX(), shooter.getY() + (double)shooter.getEyeHeight() - (double)0.15F, shooter.getZ(), true);
            } else {
                iprojectile = getArrow(world, shooter, crossbow, projectile);
                if (p_220016_6_ || p_220016_9_ != 0.0F) {
                    ((AbstractArrow)iprojectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }

            if (shooter instanceof CrossbowAttackMob) {
                CrossbowAttackMob icrossbowuser = (CrossbowAttackMob)shooter;
                LivingEntity attackTarget = icrossbowuser.getTarget();
                if (attackTarget != null) {
                    icrossbowuser.shootCrossbowProjectile(attackTarget, crossbow, iprojectile, p_220016_9_);
                }
            } else {
                Vec3 vec31 = shooter.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(p_220016_9_ * ((float)Math.PI / 180F)), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = shooter.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                iprojectile.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_220016_7_, p_220016_8_);
            }

            crossbow.hurtAndBreak(flag ? 3 : 1, shooter, (p_220017_1_) -> {
                p_220017_1_.broadcastBreakEvent(hand);
            });
            world.addFreshEntity(iprojectile);
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, p_220016_5_);
        }
    }

    private static AbstractArrow getArrow(Level world, LivingEntity shooter, ItemStack crossbow, ItemStack projectile) {
        ArrowItem arrowitem = (ArrowItem)(projectile.getItem() instanceof ArrowItem ? projectile.getItem() : Items.ARROW);
        AbstractArrow arrowEntity = arrowitem.createArrow(world, projectile, shooter);
        if (shooter instanceof Player) {
            arrowEntity.setCritArrow(true);
        }
        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() - 2 + GearData.getStat(crossbow, ItemStats.RANGED_DAMAGE));

        arrowEntity.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        arrowEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            arrowEntity.setPierceLevel((byte)i);
        }

        return arrowEntity;
    }

    public static void fireProjectiles(Level world, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float p_220014_4_, float p_220014_5_) {
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = shooter instanceof Player && ((Player)shooter).getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                if (i == 0) {
                    shootProjectile(world, shooter, hand, crossbow, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 0.0F);
                } else if (i == 1) {
                    shootProjectile(world, shooter, hand, crossbow, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, -10.0F);
                } else if (i == 2) {
                    shootProjectile(world, shooter, hand, crossbow, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 10.0F);
                }
            }
        }

        onCrossbowShot(world, shooter, crossbow);
    }

    private static float[] getShotPitches(RandomSource random) {
        boolean flag = random.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(random, flag), getRandomShotPitch(random, !flag)};
    }

    private static float getRandomShotPitch(RandomSource random, boolean p_220032_0_) {
        float f = p_220032_0_ ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void onCrossbowShot(Level p_220015_0_, LivingEntity p_220015_1_, ItemStack p_220015_2_) {
        if (p_220015_1_ instanceof ServerPlayer) {
            ServerPlayer serverplayerentity = (ServerPlayer)p_220015_1_;
            if (!p_220015_0_.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayerentity, p_220015_2_);
            }

            serverplayerentity.awardStat(Stats.ITEM_USED.get(p_220015_2_.getItem()));
        }

        clearProjectiles(p_220015_2_);
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!worldIn.isClientSide) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
            SoundEvent soundevent = this.getStartSound(i);
            SoundEvent soundevent1 = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(stack.getUseDuration() - count) / getChargeTime(stack);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), soundevent, SoundSource.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), soundevent1, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }

    }

    public static int getChargeTime(ItemStack stack) {
        float baseTime = 25 / GearData.getStat(stack, ItemStats.RANGED_SPEED);
        int quickCharge = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return Math.round(Mth.clamp(baseTime - 5 * quickCharge, MIN_CHARGE_TIME, MAX_CHARGE_TIME));
    }

    private static float getShootingPower(ItemStack stack) {
        return stack.getItem() instanceof GearCrossbowItem && containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack, false);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, ItemStats.DURABILITY);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    //endregion
}

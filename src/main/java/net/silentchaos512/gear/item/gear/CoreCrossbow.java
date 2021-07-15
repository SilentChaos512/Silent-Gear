package net.silentchaos512.gear.item.gear;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreRangedWeapon;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.ModelPropertiesHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class CoreCrossbow extends CrossbowItem implements ICoreRangedWeapon {
    private static final int MIN_CHARGE_TIME = 5;
    private static final int MAX_CHARGE_TIME = 50;

    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public CoreCrossbow() {
        super(GearHelper.getBuilder(null).defaultDurability(100));
    }

    @Override
    public GearType getGearType() {
        return GearType.CROSSBOW;
    }

    //region Crossbow stuff


    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (isCharged(itemstack)) {
            fireProjectiles(worldIn, playerIn, handIn, itemstack, getShootingPower(itemstack), 1.0F);
            setCharged(itemstack, false);
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        } else if (!playerIn.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                playerIn.startUsingItem(handIn);
            }

            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        } else {
            return new ActionResult<>(ActionResultType.FAIL, itemstack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        int i = this.getUseDuration(stack) - timeLeft;
        float f = getCharge(i, stack);
        if (f >= 1.0F && !isCharged(stack) && hasAmmo(entityLiving, stack)) {
            setCharged(stack, true);
            SoundCategory soundcategory = entityLiving instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    private static boolean hasAmmo(LivingEntity shooter, ItemStack stack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack);
        int j = i == 0 ? 1 : 3;
        boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.instabuild;
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
                if (ammo.isEmpty() && entityIn instanceof PlayerEntity) {
                    ((PlayerEntity) entityIn).inventory.removeItem(ammo);
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
        return ammo.isEmpty() || (ammo.getItem() instanceof CoreArrow && GearHelper.isBroken(ammo));
    }

    private static ItemStack splitOneAmmo(ItemStack ammo) {
        // Temp fix for https://github.com/SilentChaos512/Silent-Gear/issues/270
        if (ammo.getItem() instanceof CoreArrow) {
            ItemStack copy = ammo.copy();
            ammo.setDamageValue(ammo.getDamageValue() + 1);
            copy.setDamageValue(copy.getMaxDamage() - 1);
            return copy;
        }
        return ammo.split(1);
    }

    private static void addChargedProjectile(ItemStack crossbow, ItemStack projectile) {
        CompoundNBT compoundnbt = crossbow.getOrCreateTag();
        ListNBT listnbt;
        if (compoundnbt.contains("ChargedProjectiles", 9)) {
            listnbt = compoundnbt.getList("ChargedProjectiles", 10);
        } else {
            listnbt = new ListNBT();
        }

        CompoundNBT compoundnbt1 = new CompoundNBT();
        projectile.save(compoundnbt1);
        listnbt.add(compoundnbt1);
        compoundnbt.put("ChargedProjectiles", listnbt);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack p_220018_0_) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundNBT compoundnbt = p_220018_0_.getTag();
        if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9)) {
            ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 10);
            //noinspection ConstantConditions
            if (listnbt != null) {
                for (int i = 0; i < listnbt.size(); ++i) {
                    CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                    list.add(ItemStack.of(compoundnbt1));
                }
            }
        }

        return list;
    }

    private static void clearProjectiles(ItemStack p_220027_0_) {
        CompoundNBT compoundnbt = p_220027_0_.getTag();
        if (compoundnbt != null) {
            ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 9);
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

    private static void shootProjectile(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
        if (!world.isClientSide) {
            boolean flag = projectile.getItem() == Items.FIREWORK_ROCKET;
            ProjectileEntity iprojectile;
            if (flag) {
                iprojectile = new FireworkRocketEntity(world, projectile, shooter.getX(), shooter.getY() + (double)shooter.getEyeHeight() - (double)0.15F, shooter.getZ(), true);
            } else {
                iprojectile = getArrow(world, shooter, crossbow, projectile);
                if (p_220016_6_ || p_220016_9_ != 0.0F) {
                    ((AbstractArrowEntity)iprojectile).pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                }
            }

            if (shooter instanceof ICrossbowUser) {
                ICrossbowUser icrossbowuser = (ICrossbowUser)shooter;
                LivingEntity attackTarget = icrossbowuser.getTarget();
                if (attackTarget != null) {
                    icrossbowuser.shootCrossbowProjectile(attackTarget, crossbow, iprojectile, p_220016_9_);
                }
            } else {
                Vector3d vec3d1 = shooter.getUpVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(vec3d1), p_220016_9_, true);
                Vector3d vec3d = shooter.getViewVector(1.0F);
                Vector3f vector3f = new Vector3f(vec3d);
                vector3f.transform(quaternion);
                iprojectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), p_220016_7_, p_220016_8_);
            }

            crossbow.hurtAndBreak(flag ? 3 : 1, shooter, (p_220017_1_) -> {
                p_220017_1_.broadcastBreakEvent(hand);
            });
            world.addFreshEntity(iprojectile);
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_220016_5_);
        }
    }

    private static AbstractArrowEntity getArrow(World world, LivingEntity shooter, ItemStack crossbow, ItemStack projectile) {
        ArrowItem arrowitem = (ArrowItem)(projectile.getItem() instanceof ArrowItem ? projectile.getItem() : Items.ARROW);
        AbstractArrowEntity arrowEntity = arrowitem.createArrow(world, projectile, shooter);
        if (shooter instanceof PlayerEntity) {
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

    public static void fireProjectiles(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, float p_220014_4_, float p_220014_5_) {
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.instabuild;
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

    private static float[] getShotPitches(Random p_220028_0_) {
        boolean flag = p_220028_0_.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag), getRandomShotPitch(!flag)};
    }

    private static float getRandomShotPitch(boolean p_220032_0_) {
        float f = p_220032_0_ ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void onCrossbowShot(World p_220015_0_, LivingEntity p_220015_1_, ItemStack p_220015_2_) {
        if (p_220015_1_ instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_220015_1_;
            if (!p_220015_0_.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayerentity, p_220015_2_);
            }

            serverplayerentity.awardStat(Stats.ITEM_USED.get(p_220015_2_.getItem()));
        }

        clearProjectiles(p_220015_2_);
    }

    @Override
    public void onUseTick(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
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
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), soundevent, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                worldIn.playSound(null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), soundevent1, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }

    }

    public static int getChargeTime(ItemStack stack) {
        float baseTime = 25 / GearData.getStat(stack, ItemStats.RANGED_SPEED);
        int quickCharge = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return Math.round(MathHelper.clamp(baseTime - 5 * quickCharge, MIN_CHARGE_TIME, MAX_CHARGE_TIME));
    }

    private static float getShootingPower(ItemStack stack) {
        return stack.getItem() instanceof CoreCrossbow && containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack, false);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public int getAnimationFrames() {
        return 4;
    }

    @Override
    public int getAnimationFrame(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        IItemPropertyGetter chargedProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("charged"));
        if (chargedProperty != null && chargedProperty.call(stack, world, entity) > 0) {
            return 3;
        }

        IItemPropertyGetter pullingProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("pulling"));
        if (pullingProperty != null) {
            float pulling = pullingProperty.call(stack, world, entity);
            if (pulling > 0) {
                IItemPropertyGetter pullProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("pull"));
                if (pullProperty != null) {
                    float pull = pullProperty.call(stack, world, entity);

                    if (pull > 1.0f)
                        return 3;
                    if (pull > 0.58f)
                        return 2;
                    return 1;
                }
            }
        }
        return 0;
    }

    //endregion
}

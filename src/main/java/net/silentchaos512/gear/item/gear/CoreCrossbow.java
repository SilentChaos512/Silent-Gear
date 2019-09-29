package net.silentchaos512.gear.item.gear;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreRangedWeapon;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CoreCrossbow extends CrossbowItem implements ICoreRangedWeapon {
    private static final int MIN_CHARGE_TIME = 5;
    private static final int MAX_CHARGE_TIME = 50;

    private boolean field_220034_c = false;
    private boolean field_220035_d = false;

    public CoreCrossbow() {
        super(GearHelper.getBuilder(null).defaultMaxDamage(100));
        GearHelper.addModelTypeProperty(this);
    }

    @Override
    public GearType getGearType() {
        return GearType.CROSSBOW;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == ItemStats.RANGED_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(2));
        if (stat == ItemStats.RANGED_SPEED)
            return Optional.of(StatInstance.makeBaseMod(1));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(1));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == ItemStats.ENCHANTABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.45f));
        return Optional.empty();
    }

    //region Crossbow stuff

    @Override
    public UseAction getUseAction(ItemStack stack) {
        // FIXME: Vanilla is dumb. Non-vanilla crossbows do not render in first person correctly.
        return UseAction.NONE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (isCharged(itemstack)) {
            fireProjectiles(worldIn, playerIn, handIn, itemstack, func_220013_l(itemstack), 1.0F);
            setCharged(itemstack, false);
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        } else if (!playerIn.findAmmo(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.field_220034_c = false;
                this.field_220035_d = false;
                playerIn.setActiveHand(handIn);
            }

            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        } else {
            return new ActionResult<>(ActionResultType.FAIL, itemstack);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        int i = this.getUseDuration(stack) - timeLeft;
        float f = getCharge(i, stack);
        if (f >= 1.0F && !isCharged(stack) && hasAmmo(entityLiving, stack)) {
            setCharged(stack, true);
            SoundCategory soundcategory = entityLiving instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ITEM_CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    private static boolean hasAmmo(LivingEntity shooter, ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, stack);
        int j = i == 0 ? 1 : 3;
        boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.isCreativeMode;
        ItemStack itemstack = shooter.findAmmo(stack);
        ItemStack itemstack1 = itemstack.copy();

        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack = itemstack1.copy();
            }

            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }

            if (!func_220023_a(shooter, stack, itemstack, k > 0, flag)) {
                return false;
            }
        }

        return true;
    }

    private static boolean func_220023_a(LivingEntity entityIn, ItemStack crossbow, ItemStack ammo, boolean p_220023_3_, boolean p_220023_4_) {
        if (ammo.isEmpty()) {
            return false;
        } else {
            boolean flag = p_220023_4_ && ammo.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !p_220023_4_ && !p_220023_3_) {
                itemstack = ammo.split(1);
                if (ammo.isEmpty() && entityIn instanceof PlayerEntity) {
                    ((PlayerEntity) entityIn).inventory.deleteStack(ammo);
                }
            } else {
                itemstack = ammo.copy();
            }

            addChargedProjectile(crossbow, itemstack);
            return true;
        }
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
        projectile.write(compoundnbt1);
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
                    list.add(ItemStack.read(compoundnbt1));
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

    private static boolean hasChargedProjectile(ItemStack stack, Item ammoItem) {
        return getChargedProjectiles(stack).stream().anyMatch(s -> s.getItem() == ammoItem);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        // For vanilla crossbows, this is getChargeTime + 3
        return 133700;
    }

    private SoundEvent func_220025_a(int p_220025_1_) {
        switch(p_220025_1_) {
            case 1:
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
            case 2:
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
            case 3:
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
            default:
                return SoundEvents.ITEM_CROSSBOW_LOADING_START;
        }
    }

    private static float getCharge(int useTime, ItemStack stack) {
        float f = (float) useTime / getChargeTime(stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    private static void func_220016_a(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
        if (!world.isRemote) {
            boolean flag = projectile.getItem() == Items.FIREWORK_ROCKET;
            IProjectile iprojectile;
            if (flag) {
                iprojectile = new FireworkRocketEntity(world, projectile, shooter.posX, shooter.posY + (double)shooter.getEyeHeight() - (double)0.15F, shooter.posZ, true);
            } else {
                iprojectile = func_220024_a(world, shooter, crossbow, projectile);
                if (p_220016_6_ || p_220016_9_ != 0.0F) {
                    ((AbstractArrowEntity)iprojectile).pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                }
            }

            if (shooter instanceof ICrossbowUser) {
                ICrossbowUser icrossbowuser = (ICrossbowUser)shooter;
                icrossbowuser.shoot(icrossbowuser.getAttackTarget(), crossbow, iprojectile, p_220016_9_);
            } else {
                Vec3d vec3d1 = shooter.func_213286_i(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(vec3d1), p_220016_9_, true);
                Vec3d vec3d = shooter.getLook(1.0F);
                Vector3f vector3f = new Vector3f(vec3d);
                vector3f.func_214905_a(quaternion);
                iprojectile.shoot(vector3f.getX(), vector3f.getY(), vector3f.getZ(), p_220016_7_, p_220016_8_);
            }

            crossbow.damageItem(flag ? 3 : 1, shooter, (p_220017_1_) -> {
                p_220017_1_.sendBreakAnimation(hand);
            });
            world.addEntity((Entity)iprojectile);
            world.playSound(null, shooter.posX, shooter.posY, shooter.posZ, SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_220016_5_);
        }
    }

    private static AbstractArrowEntity func_220024_a(World world, LivingEntity shooter, ItemStack crossbow, ItemStack projectile) {
        ArrowItem arrowitem = (ArrowItem)(projectile.getItem() instanceof ArrowItem ? projectile.getItem() : Items.ARROW);
        AbstractArrowEntity arrowEntity = arrowitem.createArrow(world, projectile, shooter);
        if (shooter instanceof PlayerEntity) {
            arrowEntity.setIsCritical(true);
        }
        arrowEntity.setDamage(arrowEntity.getDamage() + GearData.getStat(crossbow, ItemStats.RANGED_DAMAGE));

        arrowEntity.setHitSound(SoundEvents.ITEM_CROSSBOW_HIT);
        arrowEntity.func_213865_o(true);
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            arrowEntity.setPierceLevel((byte)i);
        }

        return arrowEntity;
    }

    public static void fireProjectiles(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, float p_220014_4_, float p_220014_5_) {
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = func_220028_a(shooter.getRNG());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.isCreativeMode;
            if (!itemstack.isEmpty()) {
                if (i == 0) {
                    func_220016_a(world, shooter, hand, crossbow, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 0.0F);
                } else if (i == 1) {
                    func_220016_a(world, shooter, hand, crossbow, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, -10.0F);
                } else if (i == 2) {
                    func_220016_a(world, shooter, hand, crossbow, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 10.0F);
                }
            }
        }

        func_220015_a(world, shooter, crossbow);
    }

    private static float[] func_220028_a(Random p_220028_0_) {
        boolean flag = p_220028_0_.nextBoolean();
        return new float[]{1.0F, func_220032_a(flag), func_220032_a(!flag)};
    }

    private static float func_220032_a(boolean p_220032_0_) {
        float f = p_220032_0_ ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void func_220015_a(World p_220015_0_, LivingEntity p_220015_1_, ItemStack p_220015_2_) {
        if (p_220015_1_ instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_220015_1_;
            if (!p_220015_0_.isRemote) {
                CriteriaTriggers.SHOT_CROSSBOW.func_215111_a(serverplayerentity, p_220015_2_);
            }

            serverplayerentity.addStat(Stats.ITEM_USED.get(p_220015_2_.getItem()));
        }

        clearProjectiles(p_220015_2_);
    }

    @Override
    public void func_219972_a(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int p_219972_4_) {
        if (!worldIn.isRemote) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
            SoundEvent soundevent = this.func_220025_a(i);
            SoundEvent soundevent1 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(stack.getUseDuration() - p_219972_4_) / getChargeTime(stack);
            if (f < 0.2F) {
                this.field_220034_c = false;
                this.field_220035_d = false;
            }

            if (f >= 0.2F && !this.field_220034_c) {
                this.field_220034_c = true;
                worldIn.playSound(null, livingEntityIn.posX, livingEntityIn.posY, livingEntityIn.posZ, soundevent, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundevent1 != null && !this.field_220035_d) {
                this.field_220035_d = true;
                worldIn.playSound(null, livingEntityIn.posX, livingEntityIn.posY, livingEntityIn.posZ, soundevent1, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }

    }

    public static int getChargeTime(ItemStack stack) {
        float baseTime = 25 / GearData.getStat(stack, ItemStats.RANGED_SPEED);
        int quickCharge = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return Math.round(MathHelper.clamp(baseTime - 5 * quickCharge, MIN_CHARGE_TIME, MAX_CHARGE_TIME));
    }

    private static float func_220013_l(ItemStack stack) {
        return stack.getItem() instanceof CoreCrossbow && hasChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, ItemStats.ENCHANTABILITY);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
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
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
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

    //endregion
}

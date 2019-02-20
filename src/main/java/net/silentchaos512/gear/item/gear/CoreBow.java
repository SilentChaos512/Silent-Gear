package net.silentchaos512.gear.item.gear;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.api.item.ICoreRangedWeapon;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.models.ToolModel;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CoreBow extends ItemBow implements ICoreRangedWeapon {
    private static final int MIN_DRAW_DELAY = 10;
    private static final int MAX_DRAW_DELAY = 100;

    public CoreBow() {
        super(GearHelper.getBuilder(null));
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.bow;
    }

    @Override
    public String getGearClass() {
        return "bow";
    }

    //region Bow stuff

    @Override
    public float getDrawDelay(@Nonnull ItemStack stack) {
        return MathHelper.clamp(ICoreRangedWeapon.super.getDrawDelay(stack), MIN_DRAW_DELAY, MAX_DRAW_DELAY);
    }

    public float getArrowVelocity(ItemStack stack, int charge) {
        float f = charge / getDrawDelay(stack);
        f = (f * f + f * 2f) / 3f;
        return f > 1f ? 1f : f;
    }

    public float getArrowDamage(ItemStack stack) {
        return GearData.getStat(stack, CommonItemStats.RANGED_DAMAGE);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player.world.isRemote) {
            float pull = (stack.getUseDuration() - player.getItemInUseCount()) / getDrawDelay(stack);
            ToolModel.bowPull.put(GearData.getUUID(stack), pull);
        }
        super.onUsingTick(stack, player, count);
    }

    protected ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        } else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }
    // Same as vanilla bow, except it can be fired without arrows with infinity.

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        boolean hasAmmo = !findAmmo(player).isEmpty() || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        boolean isBroken = GearHelper.isBroken(stack);

        if (isBroken)
            return new ActionResult<>(EnumActionResult.PASS, stack);

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(stack,
                world, player, hand, hasAmmo);
        if (ret != null && ret.getType() == EnumActionResult.FAIL)
            return ret;

        if (!player.abilities.isCreativeMode && !hasAmmo) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        } else {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (worldIn.isRemote) {
            ToolModel.bowPull.remove(GearData.getUUID(stack));
        }

        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            boolean infiniteAmmo = player.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack ammo = this.findAmmo(player);

            int i = this.getUseDuration(stack) - timeLeft;
            // i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn,
            // (EntityPlayer) entityLiving, i, StackHelper.isValid(ammo) || infiniteAmmo);
            if (i < 0)
                return;

            if (!ammo.isEmpty() || infiniteAmmo) {
                if (ammo.isEmpty())
                    ammo = new ItemStack(Items.ARROW);

                float velocity = getArrowVelocity(stack, i);

                if ((double) velocity >= 0.1D) {
                    boolean flag1 = player.abilities.isCreativeMode || (ammo.getItem() instanceof ItemArrow && ((ItemArrow) ammo.getItem()).isInfinite(ammo, stack, player));

                    if (!worldIn.isRemote) {
                        ItemArrow itemarrow = (ItemArrow) (ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, ammo, player);
                        entityarrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 3.0F, 1.0F);

                        if (velocity == 1.0F)
                            entityarrow.setIsCritical(true);

                        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        float powerBoost = power > 0 ? power * 0.5f + 0.5f : 0.0f;
                        float damageBoost = getArrowDamage(stack);
                        entityarrow.setDamage(damageBoost + powerBoost);

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                        if (k > 0)
                            entityarrow.setKnockbackStrength(k);

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                            entityarrow.setFire(100);

                        stack.damageItem(1, player);

                        if (flag1)
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

                        worldIn.spawnEntity(entityarrow);
                    }

                    worldIn.playSound(null, player.posX, player.posY, player.posZ,
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F,
                            1.0F / (random.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

                    if (!flag1) {
                        ammo.shrink(1);

                        if (ammo.getCount() == 0)
                            player.inventory.deleteStack(ammo);
                    }

//                    player.addStat(StatList.getObjectUseStats(this));
                    // Shots fired statistic
                    // ToolHelper.incrementStatShotsFired(stack, 1);
                }
            }
        }
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.ENCHANTABILITY);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.DURABILITY);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
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

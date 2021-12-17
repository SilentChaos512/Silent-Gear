package net.silentchaos512.gear.item.gear;

import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreRangedWeapon;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.utils.MathUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class GearBowItem extends BowItem implements ICoreRangedWeapon {
    private static final int MIN_DRAW_DELAY = 10;
    private static final int MAX_DRAW_DELAY = 100;

    public GearBowItem() {
        // Max damage doesn't matter, just needs to be greater than zero
        super(GearHelper.getBaseItemProperties().defaultDurability(100));
/*        this.addPropertyOverride(new ResourceLocation("pull"), (stack, world, entity) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return !(entity.getActiveItemStack().getItem() instanceof CoreBow) ? 0.0F : (float)(stack.getUseDuration() - entity.getItemInUseCount()) / getDrawDelay(stack);
            }
        });*/
    }

    @Override
    public GearType getGearType() {
        return GearType.BOW;
    }

    //region Bow stuff

    @Override
    public float getDrawDelay(@Nonnull ItemStack stack) {
        return Mth.clamp(ICoreRangedWeapon.super.getDrawDelay(stack), MIN_DRAW_DELAY, MAX_DRAW_DELAY);
    }

    public float getArrowVelocity(ItemStack stack, int charge) {
        float f = charge / getDrawDelay(stack);
        f = (f * f + f * 2f) / 3f;
        return f > 1f ? 1f : f;
    }

    public float getArrowDamage(ItemStack stack) {
        return GearData.getStat(stack, ItemStats.RANGED_DAMAGE);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player.level.isClientSide) {
            float pull = (stack.getUseDuration() - player.getUseItemRemainingTicks()) / getDrawDelay(stack);
//            ToolModel.bowPull.put(GearData.getUUID(stack), pull);
        }
        super.onUsingTick(stack, player, count);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, Player player, @Nonnull InteractionHand hand) {
        // Same as vanilla bow, except it can be fired without arrows with infinity.
        ItemStack itemstack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(itemstack).isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemstack) > 0;

        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, world, player, hand, flag);
        if (ret != null) return ret;

        if (!player.getAbilities().instabuild && !flag) {
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        } else {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }
    }

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
                    ammoItem = new ItemStack(Items.ARROW);
                }

                float f = getArrowVelocity(stack, i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (ammoItem.getItem() instanceof ArrowItem && ((ArrowItem) ammoItem.getItem()).isInfinite(ammoItem, stack, player));
                    if (!worldIn.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (ammoItem.getItem() instanceof ArrowItem ? ammoItem.getItem() : Items.ARROW);
                        AbstractArrow arrowEntity = arrowitem.createArrow(worldIn, ammoItem, player);
                        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() - 2 + GearData.getStat(stack, ItemStats.RANGED_DAMAGE));
                        arrowEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (MathUtils.floatsEqual(f, 1f)) {
                            arrowEntity.setCritArrow(true);
                        }

                        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (powerLevel > 0) {
                            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
                        }

                        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (punchLevel > 0) {
                            arrowEntity.setKnockback(punchLevel);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            arrowEntity.setSecondsOnFire(100);
                        }

                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(p.getUsedItemHand()));
                        if (flag1 || player.getAbilities().instabuild && (ammoItem.getItem() == Items.SPECTRAL_ARROW || ammoItem.getItem() == Items.TIPPED_ARROW)) {
                            arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        worldIn.addFreshEntity(arrowEntity);
                    }

                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
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

    //endregion

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
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
    public int getItemEnchantability(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
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

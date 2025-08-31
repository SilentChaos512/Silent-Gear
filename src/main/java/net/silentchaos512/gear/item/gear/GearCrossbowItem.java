package net.silentchaos512.gear.item.gear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearRangedWeapon;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearCrossbowItem extends CrossbowItem implements GearRangedWeapon {
    private static final int MIN_CHARGE_TIME = 5;
    private static final int MAX_CHARGE_TIME = 50;

    private final Supplier<GearType> gearType;

    public GearCrossbowItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    protected Projectile createProjectile(Level pLevel, LivingEntity pShooter, ItemStack pWeapon, ItemStack pAmmo, boolean pIsCrit) {
        var projectile = super.createProjectile(pLevel, pShooter, pWeapon, pAmmo, pIsCrit);
        if (projectile instanceof AbstractArrow arrow) {
            var arrowDamage = getArrowDamage(pAmmo);
            var crossbowDamage = GearData.getProperties(pWeapon).getNumber(GearProperties.RANGED_DAMAGE);
            arrow.setBaseDamage(arrowDamage + crossbowDamage);
        }
        return projectile;
    }

    //region Crossbow stuff

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (GearHelper.isBroken(itemstack)) {
            return InteractionResult.PASS;
        }
        return super.use(level, player, hand);
    }

    // FIXME: This isn't actually used anywhere. Maybe we could do a mixin to edit the version in CrossbowItem?
    public static int getChargeDuration(ItemStack stack, LivingEntity shooter) {
        var drawSpeed = GearData.getProperties(stack).getNumber(GearProperties.DRAW_SPEED);
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 1.25F / drawSpeed);
        return Mth.floor(f * 20.0F);
    }

    private static float getPowerForTime(int timeLeft, ItemStack stack, LivingEntity shooter) {
        float f = (float)timeLeft / getChargeDuration(stack, shooter);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    //endregion

    //region Standard tool overrides

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(GearProperties.DURABILITY);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        GearHelper.inventoryTick(stack, level, entity, slot);
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

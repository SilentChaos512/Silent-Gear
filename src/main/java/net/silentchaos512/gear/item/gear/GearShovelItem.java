package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearShovelItem extends ShovelItem implements ICoreTool {
    private final Supplier<GearType> gearType;

    public GearShovelItem(Supplier<GearType> gearType) {
        super(GearHelper.DEFAULT_DUMMY_TIER, GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken or player is sneaking
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;
        // Try to let traits do their thing first
        InteractionResult result = GearHelper.onItemUse(context);
        // Make paths or whatever
        if (result == InteractionResult.PASS)
            return GearHelper.useAndCheckBroken(context, Items.NETHERITE_SHOVEL::useOn);
        return result;
    }

    @Override
    public GearType getGearType() {
        return gearType.get();
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return canPerformAction(stack, ItemAbilities.SHOVEL_DIG) && GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entity) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entity);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder);
        return builder.build();
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getDurabilityStat());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, tooltipContext, tooltip, flagIn);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }
}

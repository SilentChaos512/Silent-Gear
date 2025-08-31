package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearDiggerTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearAxeItem extends AxeItem implements GearDiggerTool {
    private final Supplier<GearType> gearType;

    public GearAxeItem(Supplier<GearType> gearType) {
        super(ToolMaterial.NETHERITE, 0f, -3f, GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        GearDiggerTool.super.onRecalculatePost(gear, player, finalProperties);
        if (!GearHelper.isBroken(gear)) {
            gear.set(DataComponents.WEAPON, new Weapon(2, 5.0f));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // No action if broken or player is sneaking
        if (GearHelper.isBroken(context.getItemInHand()) || context.getPlayer() != null && context.getPlayer().isCrouching())
            return InteractionResult.PASS;
        // Try to let traits do their thing first
        InteractionResult result = GearHelper.useOn(context);
        // Other vanilla actions (strip, scrape, wax off)
        if (result == InteractionResult.PASS) {
            return GearHelper.useAndCheckBroken(context, Items.NETHERITE_AXE::useOn);
        }
        return result;
    }

    @Override
    public GearType getGearType() {
        return gearType.get();
    }

    @Override
    public TagKey<Block> getToolBlockSet(ItemStack gear) {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        if (!GearHelper.isBroken(stack)) {
            return super.canPerformAction(stack, itemAbility);
        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return GearHelper.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!GearHelper.isBroken(stack)) {
            return super.mineBlock(stack, level, state, pos, miningEntity);
        }
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getProperties(stack).getNumberInt(getGearType().durabilityStat());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @org.jetbrains.annotations.Nullable EquipmentSlot slot) {
        GearHelper.inventoryTick(stack, level, entity, slot);
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
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }
}

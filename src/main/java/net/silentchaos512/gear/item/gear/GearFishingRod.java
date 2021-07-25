package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

public class GearFishingRod extends FishingRodItem implements ICoreTool {
    private static final Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTABILITY
    );

    private static final Collection<PartType> REQUIRED_PARTS = ImmutableSet.of(
            PartType.MAIN,
            PartType.ROD,
            PartType.BOWSTRING
    );

    private static final ImmutableList<PartType> RENDER_PARTS = ImmutableList.of(
            PartType.ROD,
            PartType.BOWSTRING,
            PartType.MAIN
    );

    private static final Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.ARMOR_DURABILITY,
            ItemStats.REPAIR_VALUE,
            ItemStats.HARVEST_LEVEL,
            ItemStats.HARVEST_SPEED,
            ItemStats.RANGED_DAMAGE,
            ItemStats.RANGED_SPEED,
            ItemStats.PROJECTILE_ACCURACY,
            ItemStats.PROJECTILE_SPEED,
            ItemStats.ARMOR,
            ItemStats.ARMOR_TOUGHNESS,
            ItemStats.MAGIC_ARMOR,
            ItemStats.KNOCKBACK_RESISTANCE
    );

    public GearFishingRod() {
        super(GearHelper.getBuilder(null).durability(100));
    }

    @Override
    public GearType getGearType() {
        return GearType.FISHING_ROD;
    }

    @Override
    public Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    public Set<ItemStat> getExcludedStats(ItemStack stack) {
        return EXCLUDED_STATS;
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS;
    }

    @Override
    public Collection<PartType> getRenderParts() {
        return RENDER_PARTS;
    }

    @Override
    public int getDamageOnHitEntity(ItemStack gear, LivingEntity target, LivingEntity attacker) {
        return 0;
    }

    @Override
    public boolean hasTexturesFor(PartType partType) {
        return REQUIRED_PARTS.contains(partType) || partType.isUpgrade();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        // Broken fishing rods cannot be used
        if (GearHelper.isBroken(stack)) {
            return InteractionResultHolder.pass(stack);
        }
        return super.use(worldIn, playerIn, handIn);
    }

    //region Standard tool overrides

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
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
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
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
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.onItemUse(context);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    //endregion
}

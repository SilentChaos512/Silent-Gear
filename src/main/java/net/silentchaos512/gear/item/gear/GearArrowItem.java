package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GearArrowItem extends ArrowItem implements ICoreItem {
    private static final Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.RANGED_DAMAGE,
            ItemStats.PROJECTILE_SPEED,
            ItemStats.PROJECTILE_ACCURACY
    );

    private static final Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.ARMOR_DURABILITY,
            ItemStats.REPAIR_VALUE,
            ItemStats.MELEE_DAMAGE,
            ItemStats.ATTACK_SPEED,
            ItemStats.ATTACK_REACH,
            ItemStats.RANGED_SPEED,
            ItemStats.ARMOR,
            ItemStats.ARMOR_TOUGHNESS,
            ItemStats.MAGIC_ARMOR,
            ItemStats.KNOCKBACK_RESISTANCE
    );

    private static final Collection<PartType> REQUIRED_PARTS = ImmutableList.of(
            PartType.MAIN,
            PartType.ROD,
            PartType.FLETCHING
    );

    public GearArrowItem(Properties builder) {
        super(builder);
    }

    @Override
    public GearType getGearType() {
        return GearType.ARROW;
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        int durability = GearData.getStatInt(stack, getDurabilityStat());
        if (durability == 0) return 1f;
        return (float) getMaxDamage(stack) / durability;
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
        return ImmutableList.of(
                PartType.ROD,
                PartType.FLETCHING,
                PartType.MAIN
        );
    }

    @Override
    public ItemStack construct(Collection<? extends IPartData> parts) {
        ItemStack result = ICoreItem.super.construct(parts);
        result.setDamageValue(result.getMaxDamage() - 64);
        return result;
    }

    @Override
    public boolean hasTexturesFor(PartType partType) {
        return REQUIRED_PARTS.contains(partType) || partType.isUpgrade();
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        GearArrowEntity arrow = new GearArrowEntity(worldIn, shooter);
        arrow.setArrowStack(stack);
        arrow.setBaseDamage(GearData.getStat(stack, ItemStats.RANGED_DAMAGE));

        if (shooter instanceof Player && !((Player) shooter).getAbilities().instabuild) {
            // Consume an arrow
            stack.setDamageValue(stack.getDamageValue() + 1);
        }

        return arrow;
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return !GearHelper.isBroken(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack usedStack = playerIn.getItemInHand(handIn);

        // Merge partial arrow stacks
        boolean used = false;
        if (usedStack.getDamageValue() > 0) {
            for (ItemStack stack : playerIn.getInventory().items) {
                if (stack.getItem() == this && stack.getDamageValue() > 0 && GearHelper.isEquivalent(usedStack, stack)) {
                    int count = stack.getMaxDamage() - stack.getDamageValue();
                    int merged = Math.min(usedStack.getDamageValue(), count);
                    usedStack.setDamageValue(usedStack.getDamageValue() - merged);
                    stack.setDamageValue(stack.getDamageValue() + merged);
                    used |= usedStack.getDamageValue() != stack.getDamageValue();

                    if (stack.getDamageValue() >= stack.getMaxDamage()) {
                        playerIn.getInventory().removeItem(stack);
                    }
                    if (usedStack.getDamageValue() <= 0) {
                        break;
                    }
                }
            }
        }

        return used ? InteractionResultHolder.success(usedStack) : InteractionResultHolder.pass(usedStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.onItemUse(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!KeyTracker.isDisplayStatsDown() && !KeyTracker.isDisplayTraitsDown() && !KeyTracker.isDisplayConstructionDown()) {
            tooltip.add(Component.literal("Do not use with vanilla crossbows, see issue #270")
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.add(TextUtil.misc("ammo", stack.getMaxDamage() - stack.getDamageValue()));

        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 256;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
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
}

package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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

public class CoreArrow extends ArrowItem implements ICoreItem {
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

    public CoreArrow(Properties builder) {
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
        result.setDamage(result.getMaxDamage() - 64);
        return result;
    }

    @Override
    public boolean hasTexturesFor(PartType partType) {
        return REQUIRED_PARTS.contains(partType) || partType.isUpgrade();
    }

    @Override
    public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {
        GearArrowEntity arrow = new GearArrowEntity(worldIn, shooter);
        arrow.setArrowStack(stack);
        arrow.setDamage(GearData.getStat(stack, ItemStats.RANGED_DAMAGE));

        if (shooter instanceof PlayerEntity && !((PlayerEntity) shooter).abilities.isCreativeMode) {
            // Consume an arrow
            stack.setDamage(stack.getDamage() + 1);
        }

        return arrow;
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, PlayerEntity player) {
        return !GearHelper.isBroken(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack usedStack = playerIn.getHeldItem(handIn);

        // Merge partial arrow stacks
        boolean used = false;
        if (usedStack.getDamage() > 0) {
            for (ItemStack stack : playerIn.inventory.mainInventory) {
                if (stack.getItem() == this && stack.getDamage() > 0 && GearHelper.isEquivalent(usedStack, stack)) {
                    int count = stack.getMaxDamage() - stack.getDamage();
                    int merged = Math.min(usedStack.getDamage(), count);
                    usedStack.setDamage(usedStack.getDamage() - merged);
                    stack.setDamage(stack.getDamage() + merged);
                    used |= usedStack.getDamage() != stack.getDamage();

                    if (stack.getDamage() >= stack.getMaxDamage()) {
                        playerIn.inventory.deleteStack(stack);
                    }
                    if (usedStack.getDamage() <= 0) {
                        break;
                    }
                }
            }
        }

        return used ? ActionResult.resultSuccess(usedStack) : ActionResult.resultPass(usedStack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return GearHelper.onItemUse(context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!KeyTracker.isDisplayStatsDown() && !KeyTracker.isDisplayTraitsDown() && !KeyTracker.isDisplayConstructionDown()) {
            tooltip.add(new StringTextComponent("Do not use with vanilla crossbows, see issue #270")
                    .mergeStyle(TextFormatting.RED));
        }

        tooltip.add(TextUtil.misc("ammo", stack.getMaxDamage() - stack.getDamage()));

        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
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
    public Rarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 256;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.getDamage() > 0;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
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
}

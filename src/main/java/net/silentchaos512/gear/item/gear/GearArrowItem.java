package net.silentchaos512.gear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearArrowItem extends ArrowItem implements GearItem {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.FLETCHING.get()
    ));

    private final Supplier<GearType> gearType;

    public GearArrowItem(Supplier<GearType> gearType) {
        super(new Properties().stacksTo(64));
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        int durability = (int) GearData.getProperties(stack).getNumber(getDurabilityStat());
        if (durability == 0) return 1f;
        return (float) getMaxDamage(stack) / durability;
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public ItemStack construct(Collection<PartInstance> parts) {
        ItemStack result = GearItem.super.construct(parts);
        result.setDamageValue(result.getMaxDamage() - 64);
        return result;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        GearArrowEntity arrow = new GearArrowEntity(level, shooter, ammo.copyWithCount(1), weapon);
        arrow.setArrowStack(ammo);
        arrow.setBaseDamage(GearData.getProperties(ammo).getNumber(GearProperties.RANGED_DAMAGE));
        return arrow;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.onItemUse(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        if (!KeyTracker.isDisplayStatsDown() && !KeyTracker.isDisplayTraitsDown() && !KeyTracker.isDisplayConstructionDown()) {
            tooltip.add(Component.literal("Do not use with vanilla crossbows, see issue #270")
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.add(TextUtil.misc("ammo", stack.getMaxDamage() - stack.getDamageValue()));

        GearClientHelper.addInformation(stack, tooltipContext, tooltip, flagIn);
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
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
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

    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColors() {
//        return (stack, tintIndex) -> Color.VALUE_WHITE;
        //noinspection OverlyLongLambda
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> ColorUtils.getBlendedColor(stack, PartTypes.ROD.get());
                case 1 -> ColorUtils.getBlendedColor(stack, PartTypes.MAIN.get());
                case 3 -> ColorUtils.getBlendedColor(stack, PartTypes.FLETCHING.get());
                default -> Color.VALUE_WHITE;
            };
        };
    }
}

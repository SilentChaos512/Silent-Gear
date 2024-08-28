package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearCurioItem extends Item implements ICoreItem {
    private static final Collection<PartType> REQUIRED_PARTS = ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.SETTING.get()
    );

    private final Supplier<GearType> gearType;
    private final String slot;

    public GearCurioItem(Supplier<GearType> gearType, String slot, Properties properties) {
        super(properties);
        this.gearType = gearType;
        this.slot = slot;
    }

    public String getSlot() {
        return slot;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.slot.equalsIgnoreCase(slot);
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        if (!ModList.get().isLoaded(Const.CURIOS)) {
            tooltip.add(TextUtil.misc("curiosNotInstalled").withStyle(ChatFormatting.RED));
        }
        GearClientHelper.addInformation(stack, tooltipContext, tooltip, flagIn);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder);
        return builder.build();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
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
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
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

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColors() {
//        return (stack, tintIndex) -> Color.VALUE_WHITE;
        //noinspection OverlyLongLambda
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> ColorUtils.getBlendedColor(stack, PartTypes.MAIN.get());
                case 2 -> ColorUtils.getBlendedColor(stack, PartTypes.SETTING.get());
                default -> Color.VALUE_WHITE;
            };
        };
    }
}

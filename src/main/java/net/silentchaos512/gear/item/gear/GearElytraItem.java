package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.compat.caelus.CaelusCompat;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearElytraItem extends ElytraItem implements ICoreArmor {
    private static final UUID ARMOR_UUID = UUID.fromString("f099f401-82f6-4565-a0b5-fd464f2dc72c");

    private static final List<PartType> REQUIRED_PARTS = ImmutableList.of(
            PartType.MAIN,
            PartType.BINDING
    );

    private final Supplier<GearType> gearType;

    public GearElytraItem(Supplier<GearType> gearType) {
        super(new Properties().stacksTo(1));
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return EquipmentSlot.CHEST.getName().equalsIgnoreCase(slot) || "back".equalsIgnoreCase(slot);
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS;
    }

    @Override
    public boolean supportsPart(ItemStack gear, PartInstance part) {
        PartType type = part.getType();
        boolean canAdd = part.get().canAddToGear(gear, part);
        boolean supported = (requiresPartOfType(part.getType()) && canAdd) || canAdd;
        return (type == PartTypes.MAIN.get() && supported)
                || type == PartTypes.LINING.get()
                || supported;
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return (int) getGearType().armorDurabilityMultiplier() * getStatInt(stack, getDurabilityStat());
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack))
            return;
        if (!(Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get()))
            damage = Mth.clamp(damage, 0, getMaxDamage(stack));
        super.setDamage(stack, damage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, t -> {
            GearHelper.onBroken(stack, t instanceof Player ? (Player) t : null, this.getEquipmentSlot(stack));
            onBroken.accept(t);
        });
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }

    @Override
    public ItemAttributeModifiers getAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        addAttributes(stack, builder, true);
        return builder.build();
    }

    public void addAttributes(ItemStack stack, ItemAttributeModifiers.Builder builder, boolean includeArmor) {
        if (GearHelper.isBroken(stack)) {
            return;
        }

        float armor = GearData.getProperties(stack).getNumber(GearProperties.ARMOR);
        if (armor > 0 && includeArmor) {
            builder.add(Attributes.ARMOR, new AttributeModifier(ARMOR_UUID, "Elytra armor modifier", armor, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.BODY);
        }
        GearHelper.addAttributeModifiers(stack, builder, false);
        CaelusCompat.tryAddFlightAttribute(builder);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        if (!ModList.get().isLoaded(Const.CAELUS)) {
            tooltip.add(TextUtil.misc("caelusNotInstalled").withStyle(ChatFormatting.RED));
        }
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

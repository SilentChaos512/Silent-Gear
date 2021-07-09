package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CoreCurio extends Item implements ICoreItem {
    private static final Collection<PartType> REQUIRED_PARTS = ImmutableList.of(
            PartType.MAIN,
            PartType.ADORNMENT
    );
    private static final ImmutableSet<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY
    );
    private static final ImmutableSet<ItemStat> EXCLUDED_STATS = ImmutableSet.copyOf(
            ItemStats.allStatsOrderedExcluding(RELEVANT_STATS) // FYI, this should NOT be done in most cases
    );

    private final GearType gearType;
    private final String slot;

    public CoreCurio(GearType gearType, String slot, Properties properties) {
        super(properties);
        this.gearType = gearType;
        this.slot = slot;
    }

    public String getSlot() {
        return slot;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (ModList.get().isLoaded(Const.CURIOS)) {
            return CuriosCompat.createProvider(stack);
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public GearType getGearType() {
        return gearType;
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.slot.equalsIgnoreCase(slot);
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
    public boolean hasTexturesFor(PartType partType) {
        return REQUIRED_PARTS.contains(partType);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!ModList.get().isLoaded(Const.CURIOS)) {
            tooltip.add(TextUtil.misc("curiosNotInstalled").mergeStyle(TextFormatting.RED));
        }
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        GearHelper.setDamage(stack, damage, super::setDamage);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, getDurabilityStat());
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

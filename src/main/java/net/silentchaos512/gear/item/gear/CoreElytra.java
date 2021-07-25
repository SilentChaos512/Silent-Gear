package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.compat.caelus.CaelusCompat;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.gear.util.TraitHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

public class CoreElytra extends ElytraItem implements ICoreArmor {
    private static final int DURABILITY_MULTIPLIER = 25;
    private static final UUID ARMOR_UUID = UUID.fromString("f099f401-82f6-4565-a0b5-fd464f2dc72c");

    private static final List<PartType> REQUIRED_PARTS = ImmutableList.of(
            PartType.MAIN,
            PartType.BINDING
    );

    private static final Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ARMOR
    );

    private static final Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.REPAIR_VALUE,
            ItemStats.HARVEST_LEVEL,
            ItemStats.HARVEST_SPEED,
            ItemStats.REACH_DISTANCE,
            ItemStats.MELEE_DAMAGE,
            ItemStats.MAGIC_DAMAGE,
            ItemStats.ATTACK_SPEED,
            ItemStats.ATTACK_REACH,
            ItemStats.RANGED_DAMAGE,
            ItemStats.RANGED_SPEED
    );

    public CoreElytra(Properties builder) {
        super(builder.durability(100));
    }

    @Override
    public GearType getGearType() {
        return GearType.ELYTRA;
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
    public boolean isValidSlot(String slot) {
        return EquipmentSlot.CHEST.getName().equalsIgnoreCase(slot) || "back".equalsIgnoreCase(slot);
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS;
    }

    @Override
    public boolean supportsPart(ItemStack gear, PartData part) {
        PartType type = part.getType();
        boolean canAdd = part.get().canAddToGear(gear, part);
        boolean supported = (requiresPartOfType(part.getType()) && canAdd) || canAdd;
        return (type == PartType.MAIN && supported)
                || type == PartType.LINING
                || supported;
    }

    @Override
    public boolean hasTexturesFor(PartType partType) {
        return partType == PartType.MAIN || partType == PartType.BINDING;
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return DURABILITY_MULTIPLIER;
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return DURABILITY_MULTIPLIER * getStatInt(stack, getDurabilityStat());
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack))
            return;
        if (!Config.Common.gearBreaksPermanently.get())
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
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        GearHelper.inventoryTick(stack, world, player, 0, true);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (ModList.get().isLoaded(Const.CURIOS)) {
            return CuriosCompat.createElytraProvider(stack, this);
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = LinkedHashMultimap.create();

        if (isValidSlot(slot.getName())) {
            addAttributes(slot.getName(), stack, multimap, true);
        }

        return multimap;
    }

    public void addAttributes(String slot, ItemStack stack, Multimap<Attribute, AttributeModifier> multimap, boolean includeArmor) {
        float armor = getStat(stack, ItemStats.ARMOR);
        if (armor > 0 && includeArmor) {
            multimap.put(Attributes.ARMOR, new AttributeModifier(ARMOR_UUID, "Elytra armor modifier", armor, AttributeModifier.Operation.ADDITION));
        }
        GearHelper.getAttributeModifiers(slot, stack, multimap, false);
        CaelusCompat.tryAddFlightAttribute(multimap);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!ModList.get().isLoaded(Const.CAELUS)) {
            tooltip.add(TextUtil.misc("caelusNotInstalled").withStyle(ChatFormatting.RED));
        }
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }
}

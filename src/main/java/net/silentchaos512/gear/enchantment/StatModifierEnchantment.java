package net.silentchaos512.gear.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.enchantment.IStatModifierEnchantment;
import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.SplitItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class StatModifierEnchantment extends Enchantment implements IStatModifierEnchantment {
    public StatModifierEnchantment(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    /**
     * Creates a modified {@link StatInstance} if needed. Return {@code null} will result in the
     * modifier being left alone.
     *
     * @param stat   The stat key
     * @param mod    The original modifier
     * @param charge The enchanted material's charged properties
     * @return A modified stat modifier which replaces the original, or null if no replacement is
     * needed
     */
    @Override
    @Nullable
    public StatInstance modifyStat(StatGearKey stat, StatInstance mod, ChargedProperties charge) {
        if (isSupportedModifierOp(mod)) {
            float modifiedStatValue = (float) getModifiedStatValue(stat, mod, charge);

            if (stat.getStat() instanceof SplitItemStat) {
                // For stats like armor, split the bonus evenly between all gear types
                SplitItemStat splitItemStat = (SplitItemStat) stat.getStat();
                if (!splitItemStat.getSplitTypes().contains(stat.getGearType())) {
                    modifiedStatValue = mod.getValue() + (modifiedStatValue - mod.getValue()) * splitItemStat.getSplitTypes().size();
                }
            }

            return mod.copySetValue(modifiedStatValue);
        }
        return null;
    }

    @SuppressWarnings("OverlyComplexMethod")
    protected double getModifiedStatValue(StatGearKey stat, StatInstance mod, ChargedProperties charge) {
        if (stat.getStat() == ItemStats.DURABILITY)
            return mod.getValue() * Math.pow(1.25, charge.getChargeValue());
        if (stat.getStat() == ItemStats.ARMOR_DURABILITY)
            return mod.getValue() * Math.pow(1.1, charge.getChargeValue());
        if (stat.getStat() == ItemStats.ENCHANTABILITY)
            return mod.getValue() * (1 + charge.getChargeLevel() * (Math.sqrt(charge.getChargeability() - 1)));
        if (stat.getStat() == ItemStats.HARVEST_LEVEL)
            return mod.getValue() + 1;
        if (stat.getStat() == ItemStats.HARVEST_SPEED)
            return mod.getValue() + 1.5 * charge.getChargeLevel() * charge.getChargeValue();
        if (stat.getStat() == ItemStats.MELEE_DAMAGE)
            return mod.getValue() + charge.getChargeValue();
        if (stat.getStat() == ItemStats.MAGIC_DAMAGE)
            return mod.getValue() + charge.getChargeValue();
        if (stat.getStat() == ItemStats.RANGED_DAMAGE)
            return mod.getValue() + charge.getChargeValue() / 2.0;
        if (stat.getStat() == ItemStats.ARMOR)
            return mod.getValue() + charge.getChargeValue() / 2.0;
        if (stat.getStat() == ItemStats.ARMOR_TOUGHNESS)
            return mod.getValue() + charge.getChargeValue() / 2.0;
        if (stat.getStat() == ItemStats.MAGIC_ARMOR)
            return mod.getValue() + charge.getChargeValue() / 2.0;

        return mod.getValue();
    }

    protected boolean isSupportedModifierOp(StatInstance mod) {
        return mod.getOp() == StatInstance.Operation.AVG
                || mod.getOp() == StatInstance.Operation.MAX
                || mod.getOp() == StatInstance.Operation.ADD;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return MaterialManager.from(stack) != null;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return !(ench instanceof IStatModifierEnchantment);
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}

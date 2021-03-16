package net.silentchaos512.gear.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.SplitItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;

public class StatModifierEnchantment extends Enchantment {
    public StatModifierEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
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
    @Nullable
    public StatInstance modifyStat(StatGearKey stat, StatInstance mod, ChargedProperties charge) {
        if (isSupportedModifierOp(mod)) {
            float modifiedStatValue = (float) getModifiedStatValue(stat, mod, charge);
            if (stat.getStat() instanceof SplitItemStat) {
                SplitItemStat splitItemStat = (SplitItemStat) stat.getStat();
                if (!splitItemStat.getSplitTypes().contains(stat.getGearType())) {
                    modifiedStatValue = mod.getValue() + (modifiedStatValue - mod.getValue()) * splitItemStat.getSplitTypes().size();
                }
            }
            return mod.copySetValue(modifiedStatValue);
        }
        return null;
    }

    private double getModifiedStatValue(StatGearKey stat, StatInstance mod, ChargedProperties charge) {
        if (stat.getStat() == ItemStats.DURABILITY)
            return mod.getValue() * Math.pow(1.5, charge.getChargeValue());
        if (stat.getStat() == ItemStats.ARMOR_DURABILITY)
            return mod.getValue() * Math.pow(1.2, charge.getChargeValue());
        if (stat.getStat() == ItemStats.ENCHANTABILITY)
            return mod.getValue() * (1 + charge.getChargeLevel() * (Math.sqrt(charge.getChargeability() - 1)));
        if (stat.getStat() == ItemStats.HARVEST_LEVEL)
            return mod.getValue() + charge.getChargeLevel();
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
            return mod.getValue() + charge.getChargeValue() / 4.0;
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
    public boolean canApply(ItemStack stack) {
        return MaterialManager.from(stack) != null;
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench) {
        return !(ench instanceof StatModifierEnchantment);
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

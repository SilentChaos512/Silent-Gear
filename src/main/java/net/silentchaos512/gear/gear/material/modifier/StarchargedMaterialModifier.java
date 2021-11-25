package net.silentchaos512.gear.gear.material.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.SplitItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StarchargedMaterialModifier extends ChargedMaterialModifier {
    public StarchargedMaterialModifier(IMaterialInstance material, int level) {
        super(material, level);
    }

    @Override
    public List<StatInstance> modifyStats(PartType partType, StatGearKey key, List<StatInstance> statMods) {
        List<StatInstance> ret = new ArrayList<>();

        if (key.getStat() == ItemStats.CHARGEABILITY) {
            return ret;
        }

        for (StatInstance mod : statMods) {
            StatInstance newMod = modifyStat(key, mod, getChargedProperties());
            ret.add(newMod != null ? newMod : mod);
        }

        return ret;
    }

    @Override
    public void appendTooltip(List<Component> tooltip) {
        MutableComponent text = getNameWithLevel();
        tooltip.add(TextUtil.withColor(text, Color.AQUAMARINE));
    }

    @Override
    public MutableComponent modifyMaterialName(MutableComponent name) {
        return getNameWithLevel().append(" ").append(name);
    }

    private MutableComponent getNameWithLevel() {
        MutableComponent text = TextUtil.translate("materialModifier", "starcharged");
        text.append(" ").append(new TranslatableComponent("enchantment.level." + level));
        return text;
    }

    @Nullable
    private static StatInstance modifyStat(StatGearKey stat, StatInstance mod, ChargedProperties charge) {
        if (stat.getStat() == ItemStats.CHARGEABILITY) {
            return null;
        }

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
    private static double getModifiedStatValue(StatGearKey stat, StatInstance mod, ChargedProperties charge) {
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

    private static boolean isSupportedModifierOp(StatInstance mod) {
        return mod.getOp() == StatInstance.Operation.AVG
                || mod.getOp() == StatInstance.Operation.MAX
                || mod.getOp() == StatInstance.Operation.ADD;
    }
}

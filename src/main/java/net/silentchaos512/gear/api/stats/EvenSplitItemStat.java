package net.silentchaos512.gear.api.stats;

import net.minecraft.ChatFormatting;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.utils.Color;

import java.util.Collection;

public class EvenSplitItemStat extends ItemStat {
    private final int splits;

    public EvenSplitItemStat(float defaultValue, float minValue, float maxValue, ChatFormatting nameColor, int splits, Properties properties) {
        super(defaultValue, minValue, maxValue, nameColor, properties);
        this.splits = splits;
    }

    public EvenSplitItemStat(float defaultValue, float minValue, float maxValue, Color nameColor, int splits, Properties properties) {
        super(defaultValue, minValue, maxValue, nameColor, properties);
        this.splits = splits;
    }

    @Override
    public float compute(float baseValue, boolean clampValue, GearType itemGearType, GearType statGearType, Collection<StatInstance> modifiers) {
        float value = super.compute(baseValue, clampValue, itemGearType, statGearType, modifiers);
        return value / this.splits;
    }
}

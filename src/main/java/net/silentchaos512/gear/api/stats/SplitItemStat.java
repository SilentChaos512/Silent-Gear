package net.silentchaos512.gear.api.stats;

import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.utils.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SplitItemStat extends ItemStat {
    private final Map<GearType, Float> splits = new HashMap<>();
    private final float splitsTotal;

    public SplitItemStat(float defaultValue, float minValue, float maxValue, TextFormatting nameColor, Map<GearType, Float> splitsIn, Properties properties) {
        super(defaultValue, minValue, maxValue, nameColor, properties);
        this.splits.putAll(splitsIn);
        this.splitsTotal = (float) this.splits.values().stream().mapToDouble(f -> f).sum();
    }

    public SplitItemStat(float defaultValue, float minValue, float maxValue, Color nameColor, Map<GearType, Float> splitsIn, Properties properties) {
        super(defaultValue, minValue, maxValue, nameColor, properties);
        this.splits.putAll(splitsIn);
        this.splitsTotal = (float) this.splits.values().stream().mapToDouble(f -> f).sum();
    }

    @Override
    public float compute(float baseValue, boolean clampValue, GearType itemGearType, GearType statGearType, Collection<StatInstance> modifiers) {
        float value = super.compute(baseValue, clampValue, itemGearType, statGearType, modifiers);
        if (!statGearType.equals(itemGearType) && this.splits.containsKey(itemGearType)) {
            return value * this.splits.get(itemGearType) / this.splitsTotal;
        }
        return value;
    }
}

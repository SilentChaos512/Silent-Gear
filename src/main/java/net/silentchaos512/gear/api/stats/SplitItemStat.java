package net.silentchaos512.gear.api.stats;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.utils.Color;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class SplitItemStat extends ItemStat {
    private final Map<GearType, Float> splits = new LinkedHashMap<>();
    private final float splitsTotal;

    public SplitItemStat(ResourceLocation name, float defaultValue, float minValue, float maxValue, ChatFormatting nameColor, Map<GearType, Float> splitsIn, Properties properties) {
        super(name, defaultValue, minValue, maxValue, nameColor, properties);
        this.splits.putAll(splitsIn);
        this.splitsTotal = (float) this.splits.values().stream().mapToDouble(f -> f).sum();
    }

    public SplitItemStat(ResourceLocation name, float defaultValue, float minValue, float maxValue, Color nameColor, Map<GearType, Float> splitsIn, Properties properties) {
        super(name, defaultValue, minValue, maxValue, nameColor, properties);
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

    public Collection<GearType> getSplitTypes() {
        return this.splits.keySet();
    }

    public float getSplitsTotal() {
        return this.splitsTotal;
    }
}

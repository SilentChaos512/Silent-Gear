package net.silentchaos512.gear.api.stats;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.utils.Color;

import java.util.Collection;

public class EvenSplitItemStat extends ItemStat {
    private final int splits;

    public EvenSplitItemStat(ResourceLocation name, float defaultValue, float minValue, float maxValue, ChatFormatting nameColor, int splits, Properties properties) {
        super(name, defaultValue, minValue, maxValue, nameColor, properties);
        this.splits = splits;
    }

    public EvenSplitItemStat(ResourceLocation name, float defaultValue, float minValue, float maxValue, Color nameColor, int splits, Properties properties) {
        super(name, defaultValue, minValue, maxValue, nameColor, properties);
        this.splits = splits;
    }

    @Override
    public float compute(float baseValue, boolean clampValue, GearType itemGearType, GearType statGearType, Collection<StatInstance> modifiers) {
        float value = super.compute(baseValue, clampValue, itemGearType, statGearType, modifiers);
        return value / this.splits;
    }
}

package net.silentchaos512.gear.api.stats;

import net.minecraft.util.ResourceLocation;

public interface IItemStat {
    ResourceLocation getStatId();

    default float getBaseValue() {
        return 0f;
    }

    default float getDefaultValue() {
        return 0f;
    }

    default float getMinimumValue() {
        return Integer.MIN_VALUE;
    }

    default float getMaximumValue() {
        return Integer.MAX_VALUE;
    }

    default StatInstance.Operation getDefaultOperation() {
        return StatInstance.Operation.AVG;
    }
}

package net.silentchaos512.gear.api.item;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.stats.ItemStat;

public interface IStatItem {
    float getStat(ItemStack stack, ItemStat stat);

    default int getStatInt(ItemStack stack, ItemStat stat) {
        return Math.round(getStat(stack, stat));
    }
}

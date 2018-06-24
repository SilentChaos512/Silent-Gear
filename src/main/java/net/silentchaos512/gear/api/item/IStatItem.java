package net.silentchaos512.gear.api.item;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.stats.ItemStat;

import javax.annotation.Nonnull;

public interface IStatItem {

    float getStat(@Nonnull ItemStack stack, @Nonnull ItemStat stat);

    default int getStatInt(@Nonnull ItemStack stack, @Nonnull ItemStat stat) {

        return Math.round(getStat(stack, stat));
    }
}

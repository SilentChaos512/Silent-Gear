package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;

import java.util.Set;

public interface ICoreRangedWeapon extends ICoreTool {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            CommonItemStats.RANGED_DAMAGE,
            CommonItemStats.RANGED_SPEED,
            CommonItemStats.DURABILITY,
            CommonItemStats.ENCHANTABILITY,
            CommonItemStats.RARITY
    );

    @Override
    default Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    default int getAnimationFrames() {
        return 4;
    }

    default float getBaseDrawDelay(ItemStack stack) {
        return 20;
    }

    default float getDrawDelay(ItemStack stack) {
        float speed = getStat(stack, CommonItemStats.RANGED_SPEED);
        if (speed <= 0) speed = 1f;
        return getBaseDrawDelay(stack) / speed;
    }
}

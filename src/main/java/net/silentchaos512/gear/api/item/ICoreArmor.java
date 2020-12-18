package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.gear.part.PartData;

import java.util.Set;

public interface ICoreArmor extends ICoreItem {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTABILITY,
            ItemStats.ARMOR,
            ItemStats.MAGIC_ARMOR,
            ItemStats.ARMOR_TOUGHNESS,
            ItemStats.KNOCKBACK_RESISTANCE
    );

    Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.REPAIR_VALUE,
            ItemStats.HARVEST_LEVEL,
            ItemStats.HARVEST_SPEED,
            ItemStats.REACH_DISTANCE,
            ItemStats.MELEE_DAMAGE,
            ItemStats.MAGIC_DAMAGE,
            ItemStats.ATTACK_SPEED,
            ItemStats.ATTACK_REACH,
            ItemStats.RANGED_DAMAGE,
            ItemStats.RANGED_SPEED
    );

    @Override
    default Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    default Set<ItemStat> getExcludedStats(ItemStack stack) {
        return EXCLUDED_STATS;
    }

    @Override
    default boolean supportsPart(ItemStack gear, PartData part) {
        PartType type = part.getType();
        return type == PartType.MAIN || type == PartType.TIP || ICoreItem.super.supportsPart(gear, part);
    }

    @Override
    default boolean hasTexturesFor(PartType partType) {
        return partType == PartType.MAIN || partType == PartType.TIP || partType == PartType.MISC_UPGRADE;
    }
}

package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.parts.PartData;

import java.util.Set;

public interface ICoreArmor extends ICoreItem {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.ARMOR,
            ItemStats.MAGIC_ARMOR,
            ItemStats.ARMOR_TOUGHNESS,
            ItemStats.DURABILITY,
            ItemStats.ENCHANTABILITY,
            ItemStats.RARITY
    );

    Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.HARVEST_LEVEL,
            ItemStats.HARVEST_SPEED,
            ItemStats.REACH_DISTANCE,
            ItemStats.MELEE_DAMAGE,
            ItemStats.MAGIC_DAMAGE,
            ItemStats.ATTACK_SPEED,
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
    default IItemColor getItemColors() {
        return ColorHandlers::getArmorColor;
    }

    @Override
    default boolean supportsPart(ItemStack gear, PartData part) {
        PartType type = part.getType();
        return type == PartType.MAIN || type == PartType.TIP;
    }

    @Override
    default boolean hasTexturesFor(PartType partType) {
        return partType == PartType.MAIN;
    }
}

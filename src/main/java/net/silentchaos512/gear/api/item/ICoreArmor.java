package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.lib.util.Color;

import java.util.Set;

public interface ICoreArmor extends ICoreItem {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTMENT_VALUE,
            ItemStats.ARMOR,
            ItemStats.MAGIC_ARMOR,
            ItemStats.ARMOR_TOUGHNESS,
            ItemStats.KNOCKBACK_RESISTANCE
    );

    Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.REPAIR_VALUE,
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
        boolean supported = ICoreItem.super.supportsPart(gear, part);
        return (type == PartType.MAIN && supported)
                || type == PartType.TIP
                || type == PartType.LINING
                || supported;
    }

    @Override
    default boolean hasTexturesFor(PartType partType) {
        return partType == PartType.MAIN
                || partType == PartType.TIP
                || partType == PartType.MISC_UPGRADE;
    }

    @Override
    default ItemStat getDurabilityStat() {
        return ItemStats.ARMOR_DURABILITY;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    default ItemColor getItemColors() {
//        return (stack, tintIndex) -> Color.VALUE_WHITE;
        //noinspection OverlyLongLambda
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> ColorUtils.getBlendedColor(stack, PartType.MAIN);
                default -> Color.VALUE_WHITE;
            };
        };
    }
}

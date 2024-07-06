package net.silentchaos512.gear.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.lib.util.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TierHelper {
    /**
     * Gets the highest Tier of all the materials or parts in the given list.
     *
     * @param components The material or part list
     * @return The highest Tier of all the materials or parts
     */
    public static Tier getHighestTier(Collection<? extends IGearComponentInstance<?>> components) {
        Tier max = weakestTier();
        for (IGearComponentInstance<?> comp : components) {
            Tier tier = comp.getHarvestTier();
            if (TierSortingRegistry.getTiersLowerThan(tier).size() > TierSortingRegistry.getTiersLowerThan(max).size()) {
                max = tier;
            }
        }
        return max;
    }

    /**
     * Gets the weakest Tier in the TierSortingRegistry, or WOOD if the registry is broken somehow.
     *
     * @return The weakest sorted Tier
     */
    public static Tier weakestTier() {
        List<Tier> sortedTiers = TierSortingRegistry.getSortedTiers();
        if (sortedTiers.size() > 0) {
            return sortedTiers.get(0);
        }
        return Tiers.WOOD;
    }

    public static Tier getHigher(Tier a, Tier b) {
        return TierSortingRegistry.getTiersLowerThan(a).size() > TierSortingRegistry.getTiersLowerThan(b).size() ? a : b;
    }

    private static Map<Tier, Color> TEXT_COLORS = new HashMap<>();

    static {
        TEXT_COLORS.put(Tiers.WOOD, new Color(0x896727));
        TEXT_COLORS.put(Tiers.STONE, new Color(0x9a9a9a));
        TEXT_COLORS.put(Tiers.IRON, new Color(0xd8d8d8));
        TEXT_COLORS.put(Tiers.DIAMOND, new Color(0x33ebcb));
        TEXT_COLORS.put(Tiers.GOLD, new Color(0xfdff70));
        TEXT_COLORS.put(Tiers.NETHERITE, new Color(0x867b86));
    }

    public static Color getTextColor(Tier tier) {
        return TEXT_COLORS.getOrDefault(tier, Color.WHITE);
    }

    public static MutableComponent getTranslatedName(Tier tier) {
        ResourceLocation name = TierSortingRegistry.getName(tier);
        if (name == null) {
            return Component.literal("null");
        }
        return Component.translatable("harvestTier." + name.getNamespace() + "." + name.getPath());
    }

    public static MutableComponent getTranslatedNameWithColor(Tier tier) {
        MutableComponent text = getTranslatedName(tier);
        Color color = getTextColor(tier);
        return TextUtil.withColor(text, color);
    }
}

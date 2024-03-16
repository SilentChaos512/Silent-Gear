package net.silentchaos512.gear.util;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;
import net.silentchaos512.gear.api.util.IGearComponentInstance;

import java.util.Collection;
import java.util.List;

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
}

package net.silentchaos512.gear.api.property;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.silentchaos512.gear.api.item.GearType;

import java.util.Collection;
import java.util.Map;

public class TierPropertyType extends GearPropertyType<Tier, TierProperty> {
    // FIXME: Fix this whenever NeoForge replaces TierSortingRegistry
    public static final Codec<TierProperty> CODEC = ResourceLocation.CODEC
            .flatXmap(
                    id -> {
                        Tier tier = Hack.TEMP_TOOL_TIERS.get(id);
                        if (tier != null) {
                            return DataResult.success(new TierProperty(tier));
                        }
                        return DataResult.error(() -> "Unknown or unsupported tool tier: " + id);
                    },
                    property -> {
                        ResourceLocation id = Hack.TEMP_TOOL_TIERS_REVERSE.get(property.value);
                        if (id != null) {
                            return DataResult.success(id);
                        }
                        return DataResult.error(() -> "Unknown of unsupported tool tier: " + property.value);
                    }
            );

    public TierPropertyType(Builder<Tier> builder) {
        super(builder);
    }

    @Override
    public Codec<TierProperty> codec() {
        return CODEC;
    }

    @Override
    public Tier compute(Tier baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<TierProperty> modifiers) {
        Tier best = baseValue;
        for (TierProperty mod : modifiers) {
            best = Hack.getBetterTier(best, mod.value);
        }
        return best;
    }

    // region Temporary tier sorting hack

    // FIXME: Delete all of this whenever NeoForge replaces TierSortingRegistry
    public static class Hack {
        private static final Map<ResourceLocation, Tier> TEMP_TOOL_TIERS = ImmutableMap.<ResourceLocation, Tier>builder()
                .put(new ResourceLocation("wood"), Tiers.WOOD)
                .put(new ResourceLocation("stone"), Tiers.STONE)
                .put(new ResourceLocation("iron"), Tiers.IRON)
                .put(new ResourceLocation("diamond"), Tiers.DIAMOND)
                .put(new ResourceLocation("gold"), Tiers.GOLD)
                .put(new ResourceLocation("netherite"), Tiers.NETHERITE)
                .build();

        private static final Map<Tier, ResourceLocation> TEMP_TOOL_TIERS_REVERSE = ImmutableMap.<Tier, ResourceLocation>builder()
                .put(Tiers.WOOD, new ResourceLocation("wood"))
                .put(Tiers.STONE, new ResourceLocation("stone"))
                .put(Tiers.IRON, new ResourceLocation("iron"))
                .put(Tiers.DIAMOND, new ResourceLocation("diamond"))
                .put(Tiers.GOLD, new ResourceLocation("gold"))
                .put(Tiers.NETHERITE, new ResourceLocation("netherite"))
                .build();

        private static final Tier[] TEMP_TOOL_TIER_SORT = {
                Tiers.WOOD,
                Tiers.GOLD,
                Tiers.STONE,
                Tiers.IRON,
                Tiers.DIAMOND,
                Tiers.NETHERITE
        };

        private static Tier getBetterTier(Tier tier1, Tier tier2) {
            int index1 = getTierSortIndex(tier1);
            int index2 = getTierSortIndex(tier2);
            if (index2 > index1) {
                return tier2;
            }
            return tier1;
        }

        private static int getTierSortIndex(Tier tier) {
            for (int i = 0; i < TEMP_TOOL_TIER_SORT.length; ++i) {
                if (TEMP_TOOL_TIER_SORT[i] == tier) {
                    return i;
                }
            }
            return -1;
        }
    }

    // endregion
}

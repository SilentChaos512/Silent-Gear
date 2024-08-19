package net.silentchaos512.gear.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;

import java.util.*;
import java.util.stream.Collectors;

public final class SynergyUtils {
    /**
     * Scale of the base synergy curve. Higher values make the curve level off more slowly and
     * produces higher base synergy values.
     */
    private static final double SYNERGY_MULTI = 1.1;
    private static final double MIN_VALUE = 0.1;
    public static final double MAX_VALUE = 2.0;
    public static final double RARITY_WEIGHT = 0.001; // was 0.005 in pre-1.21
    public static final double NO_SHARED_CATEGORY_PENALTY = 0.2;
    public static final double SHARED_CATEGORY_BONUS = 0.015;

    private SynergyUtils() {}

    public static float getSynergy(PartType partType, List<MaterialInstance> materials, Collection<TraitInstance> traits) {
        if (materials.size() < 2) {
            return 1;
        }

        // First, we add a bonus for the number of unique materials
        double synergy = getBaseSynergy(materials);

        Map<IMaterialCategory, Integer> categoryCounts = getCategoryCounts(materials);

        // Reduce synergy if all materials do not share at least one category
        if (!hasSharedCategoryOnAllMaterials(categoryCounts, materials.size())) {
            synergy -= NO_SHARED_CATEGORY_PENALTY;
        }

        // Bonus synergy for shared categories
        for (int k : categoryCounts.values()) {
            if (k > 1) {
                synergy += SHARED_CATEGORY_BONUS * k;
            }
        }

        // Reduce synergy for differences in certain properties
        MaterialInstance primary = materials.getFirst();
        final double primaryRarity = primary.getProperty(partType, GearProperties.RARITY.get());
        final double maxRarity = materials.stream()
                .mapToDouble(m -> m.getProperty(partType, GearProperties.RARITY.get()))
                .max().orElse(0);

        for (MaterialInstance material : getUniques(materials)) {
            if (maxRarity > 0) {
                float rarity = material.getProperty(partType, GearProperties.RARITY.get());
                synergy -= RARITY_WEIGHT * Math.abs(primaryRarity - rarity);
            }
        }

        // Synergy traits
        for (TraitInstance trait : traits) {
            synergy = trait.getTrait().onCalculateSynergy(synergy, trait.getLevel());
        }

        return (float) Mth.clamp(synergy, MIN_VALUE, MAX_VALUE);
    }

    private static Map<IMaterialCategory, Integer> getCategoryCounts(List<MaterialInstance> materials) {
        Map<IMaterialCategory, Integer> ret = new HashMap<>();
        for (var mat : materials) {
            for (IMaterialCategory cat : mat.getCategories()) {
                ret.merge(cat, 1, Integer::sum);
            }
        }
        return ret;
    }

    private static boolean hasSharedCategoryOnAllMaterials(Map<IMaterialCategory, Integer> categoryCounts, int materialCount) {
        for (int k : categoryCounts.values()) {
            if (k == materialCount) {
                return true;
            }
        }
        return false;
    }

    public static Collection<MaterialInstance> getUniques(Collection<? extends MaterialInstance> materials) {
        Map<ResourceLocation, MaterialInstance> ret = new LinkedHashMap<>();
        for (MaterialInstance material : materials) {
            ret.put(material.getId(), material);
        }
        return ret.values();
    }

    public static Component getDisplayText(float synergy) {
        ChatFormatting color = synergy < 1 ? ChatFormatting.RED : synergy > 1 ? ChatFormatting.GREEN : ChatFormatting.WHITE;
        Component value = Component.literal(Math.round(100 * synergy) + "%").withStyle(color);
        return TextUtil.translate("misc", "synergy", value);
    }

    private static double getBaseSynergy(Collection<? extends MaterialInstance> materials) {
        final int x = getUniqueCount(materials);
        final double a = SYNERGY_MULTI;
        return a * (x / (x + a)) + (1 / (1 + a));
    }

    private static int getUniqueCount(Collection<? extends MaterialInstance> materials) {
        return materials.stream()
                .map(MaterialInstance::getId)
                .collect(Collectors.toSet())
                .size();
    }
}

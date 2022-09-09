package net.silentchaos512.gear.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.trait.SynergyTrait;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SynergyUtils {
    /**
     * Scale of the base synergy curve. Higher values make the curve level off more slowly and
     * produces higher base synergy values.
     */
    private static final double SYNERGY_MULTI = 1.1;
    private static final double MIN_VALUE = 0.1;
    public static final double MAX_VALUE = 2.0;

    private SynergyUtils() {}

    public static float getSynergy(PartType partType, List<? extends IMaterialInstance> materials, Collection<TraitInstance> traits) {
        // TODO: Factor material categories into calculation, decrease weight of rarity and maybe tier
        //  https://github.com/SilentChaos512/Silent-Gear/issues/267

        if (materials.isEmpty()) {
            return 1;
        }

        // First, we add a bonus for the number of unique materials
        double synergy = getBaseSynergy(materials);

        // Second, reduce synergy for differences in certain properties
        IMaterialInstance primary = materials.get(0);
        final double primaryRarity = primary.getStat(partType, ItemStats.RARITY);
        final double maxRarity = materials.stream()
                .mapToDouble(m -> m.getStat(partType, ItemStats.RARITY))
                .max().orElse(0);
        final int maxTier = materials.stream()
                .mapToInt(m -> m.getTier(partType))
                .max().orElse(0);

        for (IMaterialInstance material : getUniques(materials)) {
            if (maxRarity > 0) {
                float rarity = material.getStat(partType, ItemStats.RARITY);
                synergy -= 0.005 * Math.abs(primaryRarity - rarity);
            }
            if (maxTier > 0) {
                int tier = material.getTier(partType);
                synergy -= 0.08 * Math.abs(maxTier - tier);
            }
        }

        // Synergy traits
        for (TraitInstance trait : traits) {
            if (trait.getTrait() instanceof SynergyTrait) {
                synergy = ((SynergyTrait) trait.getTrait()).apply(synergy, trait.getLevel());
            }
        }

        return (float) Mth.clamp(synergy, MIN_VALUE, MAX_VALUE);
    }

    public static Collection<IMaterialInstance> getUniques(Collection<? extends IMaterialInstance> materials) {
        Map<ResourceLocation, IMaterialInstance> ret = new LinkedHashMap<>();
        for (IMaterialInstance material : materials) {
            ret.put(material.getId(), material);
        }
        return ret.values();
    }

    public static Component getDisplayText(float synergy) {
        ChatFormatting color = synergy < 1 ? ChatFormatting.RED : synergy > 1 ? ChatFormatting.GREEN : ChatFormatting.WHITE;
        Component value = Component.literal(Math.round(100 * synergy) + "%").withStyle(color);
        return TextUtil.translate("misc", "synergy", value);
    }

    private static double getBaseSynergy(Collection<? extends IMaterialInstance> materials) {
        final int x = getUniqueCount(materials);
        final double a = SYNERGY_MULTI;
        return a * (x / (x + a)) + (1 / (1 + a));
    }

    private static int getUniqueCount(Collection<? extends IMaterialInstance> materials) {
        return materials.stream()
                .map(IMaterialInstance::getId)
                .collect(Collectors.toSet())
                .size();
    }
}

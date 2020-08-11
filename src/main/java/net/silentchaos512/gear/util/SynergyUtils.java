package net.silentchaos512.gear.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.traits.SynergyTrait;

import java.util.*;
import java.util.stream.Collectors;

public class SynergyUtils {
    /**
     * Scale of the base synergy curve. Higher values make the curve level off more slowly and
     * produces higher base synergy values.
     */
    private static final double SYNERGY_MULTI = 1.1;
    private static final double MIN_VALUE = 0.1;
    public static final double MAX_VALUE = 2.0;

    public static float getSynergy(PartType partType, List<? extends IMaterialInstance> materials, List<PartTraitInstance> traits) {
        if (materials.isEmpty()) {
            return 1;
        }

        // First, we add a bonus for the number of unique materials
        double synergy = getBaseSynergy(materials);

        // Second, reduce synergy for differences in certain properties
        IMaterialInstance primary = materials.get(0);
        final double primaryRarity = primary.getStat(ItemStats.RARITY, partType);
        final double maxRarity = materials.stream()
                .mapToDouble(m -> m.getStat(ItemStats.RARITY, partType))
                .max().orElse(0);
        final int maxTier = materials.stream()
                .mapToInt(m -> m.getTier(partType))
                .max().orElse(0);

        for (IMaterialInstance material : getUniques(materials)) {
            if (maxRarity > 0) {
                float rarity = material.getStat(ItemStats.RARITY, partType);
                synergy -= 0.005 * Math.abs(primaryRarity - rarity);
            }
            if (maxTier > 0) {
                int tier = material.getTier(partType);
                synergy -= 0.16 * Math.abs(maxTier - tier);
            }
        }

        // Synergy traits
        for (PartTraitInstance trait : traits) {
            if (trait.getTrait() instanceof SynergyTrait) {
                synergy = ((SynergyTrait) trait.getTrait()).apply(synergy, trait.getLevel());
            }
        }

        return (float) MathHelper.clamp(synergy, MIN_VALUE, MAX_VALUE);
    }

    public static Collection<IMaterialInstance> getUniques(Collection<? extends IMaterialInstance> materials) {
        Map<ResourceLocation, IMaterialInstance> ret = new LinkedHashMap<>();
        for (IMaterialInstance material : materials) {
            ret.put(material.getMaterialId(), material);
        }
        return ret.values();
    }

    public static ITextComponent getDisplayText(float synergy) {
        TextFormatting color = synergy < 1 ? TextFormatting.RED : synergy > 1 ? TextFormatting.GREEN : TextFormatting.WHITE;
        ITextComponent value = new StringTextComponent(Math.round(100 * synergy) + "%").mergeStyle(color);
        return TextUtil.translate("misc", "synergy", value);
    }

    private static double getBaseSynergy(Collection<? extends IMaterialInstance> materials) {
        final int x = getUniqueCount(materials);
        final double a = SYNERGY_MULTI;
        return a * (x / (x + a)) + (1 / (1 + a));
    }

    private static int getUniqueCount(Collection<? extends IMaterialInstance> materials) {
        return materials.stream()
                .map(IMaterialInstance::getMaterialId)
                .collect(Collectors.toSet())
                .size();
    }
}

package net.silentchaos512.gear.client.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ColorUtils {
    private ColorUtils() {
    }

    public static int getBlendedColor(GearItem item, PartInstance part, Collection<? extends MaterialInstance> materials) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int i = 0;
        for (MaterialInstance mat : materials) {
            int color = mat.getColor(item.getGearType(), part.getType());
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            int colorWeight = (materials.size() - i) * (materials.size() - i);
            for (int j = 0; j < colorWeight; ++j) {
                maxColorSum += Math.max(r, Math.max(g, b));
                componentSums[0] += r;
                componentSums[1] += g;
                componentSums[2] += b;
                ++colorCount;
            }
            ++i;
        }

        return blendColors(componentSums, maxColorSum, colorCount);
    }

    @Deprecated(forRemoval = true)
    public static int getBlendedColor(CompoundPartItem item, Collection<? extends MaterialInstance> materials) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int i = 0;
        for (MaterialInstance mat : materials) {
            int color = mat.getColor(item.getGearType(), item.getPartType());
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            int colorWeight = item.getColorWeight(i, materials.size());
            for (int j = 0; j < colorWeight; ++j) {
                maxColorSum += Math.max(r, Math.max(g, b));
                componentSums[0] += r;
                componentSums[1] += g;
                componentSums[2] += b;
                ++colorCount;
            }
            ++i;
        }

        return blendColors(componentSums, maxColorSum, colorCount);
    }

    public static int getBlendedColorForCompoundMaterial(Collection<? extends MaterialInstance> materials) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        for (MaterialInstance mat : materials) {
            int color = mat.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get());
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            maxColorSum += Math.max(r, Math.max(g, b));
            componentSums[0] += r;
            componentSums[1] += g;
            componentSums[2] += b;
            ++colorCount;
        }

        return blendColors(componentSums, maxColorSum, colorCount);
    }

    private static int blendColors(int[] componentSums, float maxColorSum, int colorCount) {
        if (colorCount > 0) {
            int r = componentSums[0] / colorCount;
            int g = componentSums[1] / colorCount;
            int b = componentSums[2] / colorCount;
            float maxAverage = maxColorSum / (float) colorCount;
            float max = (float) Math.max(r, Math.max(g, b));
            r = (int) ((float) r * maxAverage / max);
            g = (int) ((float) g * maxAverage / max);
            b = (int) ((float) b * maxAverage / max);
            int finalColor = (r << 8) + g;
            finalColor = (finalColor << 8) + b;
            return finalColor | 0xFF000000;
        }

        return 0xFFFFFFFF;
    }

    public static int getBlendedColorForPartInGear(ItemStack stack, PartType partType) {
        if (hasCachedColor(stack, partType, 0)) {
            return getCachedColor(stack, partType, 0);
        }

        // Calculate and cache the layer color
        var list = GearData.getConstruction(stack).parts().getPartsOfType(partType);
        if (!list.isEmpty()) {
            var part = list.getFirst();
            int color = part.getColor(GearHelper.getType(stack), 0, 0);
            setCachedColor(stack, partType, 0, color);
            return color;
        }
        return 0xFFFFFFFF;
    }

    public static final Cache<String, Map<PartType, Integer>> GEAR_COLOR_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public static boolean hasCachedColor(ItemStack stack, PartType partType, int animationFrame) {
        String modelKey = GearData.getModelKey(stack, animationFrame);
        Map<PartType, Integer> map = GEAR_COLOR_CACHE.getIfPresent(modelKey);
        return map != null && map.containsKey(partType);
    }

    public static int getCachedColor(ItemStack stack, PartType partType, int animationFrame) {
        Map<PartType, Integer> partTypeMap = GEAR_COLOR_CACHE.getIfPresent(GearData.getModelKey(stack, animationFrame));
        if (partTypeMap != null) {
            return partTypeMap.getOrDefault(partType, 0xFFFFFFFF);
        }
        return 0xFFFFFFFF;
    }

    public static void setCachedColor(ItemStack stack, PartType partType, int animationFrame, int color) {
        String modelKey = GearData.getModelKey(stack, animationFrame);
        Map<PartType, Integer> map = GEAR_COLOR_CACHE.getIfPresent(modelKey);
        if (map == null) {
            map = new HashMap<>();
            GEAR_COLOR_CACHE.put(modelKey, map);
        }
        map.put(partType, color);
    }
}

package net.silentchaos512.gear.client.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.ColorBlendAlgorithm;
import net.silentchaos512.lib.util.ColorUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class GearColorUtils {
    private GearColorUtils() {
    }

    public static int getBlendedColorForCompoundMaterial(Collection<? extends MaterialInstance> materials) {
        var colorsIntList = materials.stream()
                .map(mat -> mat.getColor(GearTypes.ALL, PartTypes.MAIN))
                .toList();
        return ColorUtils.blend(ColorBlendAlgorithm.MIXBOX, colorsIntList);
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

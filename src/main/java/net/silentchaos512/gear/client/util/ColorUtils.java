package net.silentchaos512.gear.client.util;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.utils.Color;

import java.util.Collection;
import java.util.List;

public final class ColorUtils {
    private ColorUtils() {}

    public static int getBlendedColor(ICoreItem item, IPartData part, Collection<? extends IMaterialInstance> materials, int layer) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int i = 0;
        for (IMaterialInstance mat : materials) {
            IMaterialDisplay model = MaterialDisplayManager.getMaterial(mat.getId());
            int color = model.getLayerColor(item.getGearType(), part, mat, layer);
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

    public static int getBlendedColor(CompoundPartItem item, Collection<? extends IMaterialInstance> materials, int layer) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int i = 0;
        for (IMaterialInstance mat : materials) {
            IMaterialDisplay model = MaterialDisplayManager.getMaterial(mat.getId());
            List<MaterialLayer> layers = model.getLayerList(item.getGearType(), item.getPartType(), mat).getLayers();
            if (layers.size() > layer) {
                int color = model.getLayerColor(item.getGearType(), item.getPartType(), mat, layer);
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
        }

        return blendColors(componentSums, maxColorSum, colorCount);
    }

    public static int getBlendedColor(CompoundMaterialItem item, Collection<? extends IMaterialInstance> materials, int layer) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int i = 0;
        for (IMaterialInstance mat : materials) {
            IMaterialDisplay model = MaterialDisplayManager.get(mat.getMaterial());
            List<MaterialLayer> layers = model.getLayerList(GearType.ALL, PartType.MAIN, mat).getLayers();
            if (layers.size() > layer) {
                int color = layers.get(layer).getColor();
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
            return finalColor;
        }

        return Color.VALUE_WHITE;
    }
}

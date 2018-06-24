package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.client.util.EquipmentClientHelper;
import net.silentchaos512.gear.init.ModItems;

import java.util.HashMap;
import java.util.Map;

public class ColorHandlers {

    public static Map<String, Integer[]> toolColorCache = new HashMap<>();

    public static void init() {
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        // Tools - mostly used for broken color, but colors could be changed at any time
        itemColors.registerItemColorHandler((stack, tintIndex) -> {

            if (!(stack.getItem() instanceof ICoreItem) || tintIndex < 0)
                return 0xFFFFFF;

            String modelKey = EquipmentClientHelper.getModelKey(stack, 0);
            Integer[] colors = toolColorCache.get(modelKey);

            if (colors != null && tintIndex < colors.length) {
                return colors[tintIndex];
            }
            return 0xFFFFFF;
        }, ModItems.toolClasses.values().stream().map(Item.class::cast).toArray(Item[]::new));
    }
}

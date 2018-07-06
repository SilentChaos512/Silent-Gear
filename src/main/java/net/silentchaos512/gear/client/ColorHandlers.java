package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColorHandlers {

    public static Map<String, Integer[]> gearColorCache = new HashMap<>();

    public static void init() {
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        List<Item> gearItems = new ArrayList<>();
        gearItems.addAll(ModItems.toolClasses.values().stream().map(ICoreItem::getItem).collect(Collectors.toList()));
        gearItems.addAll(ModItems.armorClasses.values().stream().map(ICoreItem::getItem).collect(Collectors.toList()));

        // Tools/Armor - mostly used for broken color, but colors could be changed at any time
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (!(stack.getItem() instanceof ICoreItem) || tintIndex < 0)
                return 0xFFFFFF;

            String modelKey = ((ICoreItem) stack.getItem()).getModelKey(stack, 0);
            Integer[] colors = gearColorCache.get(modelKey);
            return colors != null && tintIndex < colors.length ? colors[tintIndex] : 0xFFFFFF;
        }, gearItems.toArray(new Item[0]));

        // Tool Heads
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            String modelKey = ModItems.toolHead.getModelKey(stack);
            Integer[] colors = gearColorCache.get(modelKey);
            return colors != null && tintIndex < colors.length ? colors[tintIndex] : 0xFFFFFF;
        }, ModItems.toolHead);
    }
}

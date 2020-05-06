package net.silentchaos512.gear.client;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CustomTippedUpgrade;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.utils.Color;

import java.util.HashMap;
import java.util.Map;

public final class ColorHandlers {
    public static Map<String, Integer[]> gearColorCache = new HashMap<>();

    private ColorHandlers() {}

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        if (itemColors == null) {
            SilentGear.LOGGER.error("ItemColors is null?", new IllegalStateException("wat?"));
            return;
        }

        itemColors.register(CustomTippedUpgrade::getItemColor, ModItems.customTippedUpgrade);

        // Tools/Armor - mostly used for broken color, but colors could be changed at any time
        itemColors.register(ColorHandlers::getToolLiteColor,
                ModItems.toolClasses.values()
                        .stream()
                        .map(ICoreItem::asItem)
                        .toArray(Item[]::new)
        );
        itemColors.register(ColorHandlers::getArmorLiteColor,
                ModItems.armorClasses.values()
                        .stream()
                        .map(ICoreItem::asItem)
                        .toArray(Item[]::new)
        );
        itemColors.register(ColorHandlers::getShieldColor, ModItems.shield);
    }

    // TODO: When models get fixed, switch back to this
    @SuppressWarnings("unused")
    public static int getGearLayerColor(ItemStack stack, int tintIndex) {
        if (!(stack.getItem() instanceof ICoreItem) || tintIndex < 0) return Color.VALUE_WHITE;

        String modelKey = GearData.getCachedModelKey(stack, 0);
        Integer[] colors = gearColorCache.get(modelKey);
        return colors != null && tintIndex < colors.length ? colors[tintIndex] : Color.VALUE_WHITE;
    }

    // Temporary rendering solution until Forge fixes their stuff
    public static int getToolLiteColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            // Rod
            PartData part = GearData.getPartOfType(stack, PartType.ROD);
            if (part == null) return Color.VALUE_WHITE;
            return part.getFallbackColor(stack, 0);
        }
        if (tintIndex == 1) {
            // Grip
            PartData part = GearData.getPartOfType(stack, PartType.GRIP);
            if (part == null) return Color.VALUE_WHITE;
            return part.getFallbackColor(stack, 0);
        }
        if (tintIndex == 2) {
            // Main
            return GearData.getHeadColor(stack, true);
        }
        if (tintIndex == 3) {
            // Tip
            PartData part = GearData.getPartOfType(stack, PartType.TIP);
            if (part == null) return Color.VALUE_WHITE;
            return part.getFallbackColor(stack, 0);
        }
        return Color.VALUE_WHITE;
    }

    public static int getArmorLiteColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            // Main
            return GearData.getHeadColor(stack, true);
        }
        if (tintIndex == 1) {
            // Tip
            PartData part = GearData.getPartOfType(stack, PartType.TIP);
            if (part == null) return Color.VALUE_WHITE;
            return part.getFallbackColor(stack, 0);
        }
        return Color.VALUE_WHITE;
    }

    private static int getShieldColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            PartData part = GearData.getPartOfType(stack, PartType.ROD);
            if (part == null) return Color.VALUE_WHITE;
            return part.getFallbackColor(stack, 0);
        }
        if (tintIndex == 1) {
            return GearData.getHeadColor(stack, true);
        }
        return 0xFFFFFF;
    }
}

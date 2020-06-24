package net.silentchaos512.gear.client;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.CustomTippedUpgrade;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.utils.Color;

import java.util.HashMap;
import java.util.Map;

public final class ColorHandlers {
    @Deprecated
    public static Map<String, Integer[]> gearColorCache = new HashMap<>();

    private ColorHandlers() {}

    public static void onItemColors(ColorHandlerEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        if (itemColors == null) {
            SilentGear.LOGGER.error("ItemColors is null?", new IllegalStateException("wat?"));
            return;
        }

        itemColors.register(CustomTippedUpgrade::getItemColor, ModItems.CUSTOM_TIPPED_UPGRADE);

        // Tools, armor, shields, etc.
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof ICoreItem)
                .map(item -> (ICoreItem) item)
                .forEach(item -> itemColors.register(item.getItemColors(), item));

        // Compound part items
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof CompoundPartItem)
                .forEach(item -> itemColors.register(((CompoundPartItem) item)::getColor, item));
    }

    /**
     * Standard colors for most tools, melee weapons, and ranged weapons
     *
     * @param stack     The item
     * @param tintIndex The tint index
     * @return The color of the layer
     */
    public static int getToolColor(ItemStack stack, int tintIndex) {
        switch (tintIndex) {
            case 0:
                return GearData.getColor(stack, PartType.ROD);
            case 1:
                return GearData.getColor(stack, PartType.GRIP);
            case 2:
                return GearData.getColor(stack, PartType.MAIN);
            case 3:
                return GearData.getColor(stack, PartType.TIP);
            default:
                return Color.VALUE_WHITE;
        }
    }

    @Deprecated
    public static int getToolLiteColor(ItemStack stack, int tintIndex) {
        return getToolColor(stack, tintIndex);
    }

    /**
     * Standard colors for armor items
     *
     * @param stack     The item
     * @param tintIndex The tint index
     * @return The color of the layer
     */
    public static int getArmorColor(ItemStack stack, int tintIndex) {
        switch (tintIndex) {
            case 0:
                return GearData.getColor(stack, PartType.MAIN);
            case 1:
                return GearData.getColor(stack, PartType.TIP);
            default:
                return Color.VALUE_WHITE;
        }
    }

    @Deprecated
    public static int getArmorLiteColor(ItemStack stack, int tintIndex) {
        return getArmorColor(stack, tintIndex);
    }

    /**
     * Standard colors for shields
     *
     * @param stack     The item
     * @param tintIndex The tint index
     * @return The color of the layer
     */
    public static int getShieldColor(ItemStack stack, int tintIndex) {
        switch (tintIndex) {
            case 0:
                return GearData.getColor(stack, PartType.ROD);
            case 1:
                return GearData.getColor(stack, PartType.MAIN);
            default:
                return Color.VALUE_WHITE;
        }
    }
}

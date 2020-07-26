package net.silentchaos512.gear.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.item.gear.CoreShield;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.registry.ItemRegistryObject;
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

        // Tools, armor, shields, etc.
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof CoreArmor || item instanceof CoreShield)
                .map(item -> (ICoreItem) item)
                .forEach(item -> itemColors.register(item.getItemColors(), item));
    }

    private static void register(ItemColors colors, IItemColor itemColor, ItemRegistryObject<? extends Item> item) {
        if (item.getRegistryObject().isPresent()) {
            colors.register(itemColor, item.get());
        } else {
            SilentGear.LOGGER.error("Failed to add color handler for {}: item not present", item.getRegistryObject().getId());
        }
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
                return GearData.getBlendedColor(stack, PartType.ROD);
            case 1:
                return GearData.getBlendedColor(stack, PartType.GRIP);
            case 2:
                return GearData.getBlendedColor(stack, PartType.MAIN);
            case 3:
                return GearData.getBlendedColor(stack, PartType.TIP);
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
                return GearData.getBlendedColor(stack, PartType.MAIN);
            case 1:
                return GearData.getBlendedColor(stack, PartType.TIP);
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
                return GearData.getBlendedColor(stack, PartType.ROD);
            case 1:
                return GearData.getBlendedColor(stack, PartType.MAIN);
            default:
                return Color.VALUE_WHITE;
        }
    }
}

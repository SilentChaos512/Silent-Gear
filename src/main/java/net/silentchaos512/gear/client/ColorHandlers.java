package net.silentchaos512.gear.client;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.IColoredMaterialItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.utils.Color;

import java.util.HashMap;
import java.util.Map;

public final class ColorHandlers {
    @Deprecated
    public static Map<String, Integer[]> gearColorCache = new HashMap<>();

    private ColorHandlers() {
    }

    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        if (itemColors == null) {
            SilentGear.LOGGER.error("ItemColors is null?", new IllegalStateException("wat?"));
            return;
        }

        // Tools, armor, shields, etc.
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof ICoreItem /*item instanceof GearArmorItem || item instanceof GearShieldItem*/)
                .map(item -> (ICoreItem) item)
                .forEach(item -> event.register(item.getItemColors(), item));

        // Gear parts
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof CompoundPartItem)
                .map(item -> (CompoundPartItem) item)
                .forEach(item -> event.register(item::getColor, item));

        // Compound/custom materials
        SgItems.getItems(item -> item instanceof IColoredMaterialItem).forEach(item -> {
            IColoredMaterialItem coloredMaterialItem = (IColoredMaterialItem) item;
            event.register(coloredMaterialItem::getColor, item);
        });
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

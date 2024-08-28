package net.silentchaos512.gear.client;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.IColoredMaterialItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.util.Color;

public final class ColorHandlers {
    private ColorHandlers() {
    }

    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        if (itemColors == null) {
            SilentGear.LOGGER.error("ItemColors is null?", new IllegalStateException("wat?"));
            return;
        }

        // Tools, armor, shields, etc.
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof GearItem /*item instanceof GearArmorItem || item instanceof GearShieldItem*/)
                .map(item -> (GearItem) item)
                .forEach(item -> event.register(item.getItemColors(), item));

        // Gear parts
        BuiltInRegistries.ITEM.stream()
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
                return ColorUtils.getBlendedColor(stack, PartTypes.ROD.get());
            case 1:
                return ColorUtils.getBlendedColor(stack, PartTypes.MAIN.get());
            default:
                return Color.VALUE_WHITE;
        }
    }
}

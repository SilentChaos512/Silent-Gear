package net.silentchaos512.gear.core;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearHelper;

public record ToolColors(
        int main,
        int coating,
        int rod,
        int grip,
        int tip
) {
    public static ToolColors from(ItemStack gear) {
        if (!GearHelper.isGear(gear)) {
            throw new IllegalStateException("Not a gear item: " + gear);
        }

        return new ToolColors(
                ColorUtils.getBlendedColorForPartInGear(gear, PartTypes.MAIN),
                ColorUtils.getBlendedColorForPartInGear(gear, PartTypes.COATING),
                ColorUtils.getBlendedColorForPartInGear(gear, PartTypes.ROD),
                ColorUtils.getBlendedColorForPartInGear(gear, PartTypes.GRIP),
                ColorUtils.getBlendedColorForPartInGear(gear, PartTypes.TIP)
        );
    }
}

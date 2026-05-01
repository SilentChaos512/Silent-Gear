package net.silentchaos512.gear.core;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.util.GearColorUtils;
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
                GearColorUtils.getBlendedColorForPartInGear(gear, PartTypes.MAIN.get()),
                GearColorUtils.getBlendedColorForPartInGear(gear, PartTypes.COATING.get()),
                GearColorUtils.getBlendedColorForPartInGear(gear, PartTypes.ROD.get()),
                GearColorUtils.getBlendedColorForPartInGear(gear, PartTypes.GRIP.get()),
                GearColorUtils.getBlendedColorForPartInGear(gear, PartTypes.TIP.get())
        );
    }
}

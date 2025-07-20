package net.silentchaos512.gear.api.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;

public interface GearArmor extends GearItem {
    @Override
    default boolean supportsPart(ItemStack gear, PartInstance part) {
        PartType type = part.getType();
        boolean supported = GearItem.super.supportsPart(gear, part);
        return (type == PartTypes.MAIN.get() && supported)
                || type == PartTypes.TIP.get()
                || type == PartTypes.LINING.get()
                || supported;
    }
}

package net.silentchaos512.gear.item.gear;

import net.minecraft.world.item.Item;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class GearMaceItem extends Item {
    private final Supplier<GearType> gearType;

    public GearMaceItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }
}

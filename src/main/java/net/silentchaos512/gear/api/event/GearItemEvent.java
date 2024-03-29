package net.silentchaos512.gear.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.gear.part.PartData;

import java.util.Collection;

public abstract class GearItemEvent extends Event {
    private final ItemStack gear;
    private final PartDataList parts;

    public GearItemEvent(ItemStack gear, Collection<PartData> parts) {
        this.gear = gear;
        this.parts = PartDataList.of(parts);
    }

    public ItemStack getGear() {
        return gear;
    }

    public PartDataList getParts() {
        return PartDataList.of(parts);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}

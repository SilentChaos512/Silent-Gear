package net.silentchaos512.gear.api.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.gear.part.PartInstance;

import java.util.Collection;

public abstract class GearItemEvent extends Event {
    private final ItemStack gear;
    private final PartList parts;

    public GearItemEvent(ItemStack gear, Collection<PartInstance> parts) {
        this.gear = gear;
        this.parts = PartList.of(parts);
    }

    public ItemStack getGear() {
        return gear;
    }

    public PartList getParts() {
        return PartList.of(parts);
    }
}

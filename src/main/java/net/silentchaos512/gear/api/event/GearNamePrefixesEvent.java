package net.silentchaos512.gear.api.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.part.PartInstance;

import java.util.ArrayList;
import java.util.Collection;

public class GearNamePrefixesEvent extends GearItemEvent {
    private final Collection<Component> prefixes = new ArrayList<>();

    public GearNamePrefixesEvent(ItemStack gear, Collection<PartInstance> parts) {
        super(gear, parts);
        parts.forEach(p -> {
            var gearPart = p.getNullable();
            if (gearPart != null) {
                prefixes.add(gearPart.getDisplayNamePrefix(p, gear));
            }
        });
    }

    public Collection<Component> getPrefixes() {
        return prefixes;
    }
}

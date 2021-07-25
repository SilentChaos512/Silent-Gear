package net.silentchaos512.gear.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.gear.part.PartData;

import java.util.ArrayList;
import java.util.Collection;

public class GearNamePrefixesEvent extends GearItemEvent {
    private final Collection<Component> prefixes = new ArrayList<>();

    public GearNamePrefixesEvent(ItemStack gear, Collection<PartData> parts) {
        super(gear, parts);
        parts.forEach(p -> {
            Component prefix = p.get().getDisplayNamePrefix(p, gear);
            if (prefix != null) {
                prefixes.add(prefix);
            }
        });
    }

    public Collection<Component> getPrefixes() {
        return prefixes;
    }
}

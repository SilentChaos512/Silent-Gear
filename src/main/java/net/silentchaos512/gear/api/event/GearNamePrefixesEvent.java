package net.silentchaos512.gear.api.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.parts.PartData;

import java.util.ArrayList;
import java.util.Collection;

public class GearNamePrefixesEvent extends GearItemEvent {
    private final Collection<ITextComponent> prefixes = new ArrayList<>();

    public GearNamePrefixesEvent(ItemStack gear, Collection<PartData> parts) {
        super(gear, parts);
        parts.forEach(p -> {
            ITextComponent prefix = p.getPart().getDisplayNamePrefix(p, gear);
            if (prefix != null) {
                prefixes.add(prefix);
            }
        });
    }

    public Collection<ITextComponent> getPrefixes() {
        return prefixes;
    }
}

package net.silentchaos512.gear.api.property;

import net.silentchaos512.lib.util.Color;

import java.util.Set;

public interface GearPropertyGroup {
    String getName();

    Color getColor();

    Set<GearProperty<?, ?>> getProperties();
}

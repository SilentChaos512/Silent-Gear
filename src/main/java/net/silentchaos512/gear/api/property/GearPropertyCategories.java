package net.silentchaos512.gear.api.property;

import net.silentchaos512.lib.util.Color;

import java.util.Locale;

public enum GearPropertyCategories implements GearPropertyCategory {
    // Mods are free to add their own categories, but these are the standard ones
    TRAITS(Color.GOLDENROD),
    GENERAL(Color.STEELBLUE),
    HARVEST(Color.SEAGREEN),
    ATTACK(Color.SANDYBROWN),
    PROJECTILE(Color.SKYBLUE),
    ARMOR(Color.VIOLET);

    private final Color color;

    GearPropertyCategories(Color color) {
        this.color = color;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Color getColor() {
        return color;
    }
}

package net.silentchaos512.gear.api.property;

import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.lib.util.Color;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public enum GearPropertyGroups implements GearPropertyGroup {
    // Mods are free to add their own categories, but these are the standard ones
    // TODO: Add a helper method to return all registered gear properties sorted by group
    TRAITS(Color.GOLDENROD),
    GENERAL(Color.STEELBLUE),
    HARVEST(Color.SEAGREEN),
    ATTACK(Color.SANDYBROWN),
    PROJECTILE(Color.SKYBLUE),
    ARMOR(Color.VIOLET);

    private final Color color;

    GearPropertyGroups(Color color) {
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

    @Override
    public Set<GearProperty<?, ?>> getProperties() {
        Set<GearProperty<?, ?>> set = new LinkedHashSet<>();
        for (GearProperty<?, ? extends GearPropertyValue<?>> property : SgRegistries.GEAR_PROPERTY) {
            if (property.getGroup() == this) {
                set.add(property);
            }
        }
        return set;
    }

    public static Set<GearProperty<?, ?>> getSortedRelevantProperties(Set<GearPropertyGroup> relevantPropertyGroups) {
        Set<GearProperty<?, ?>> result = new LinkedHashSet<>();
        for (GearPropertyGroups group : values()) {
            if (relevantPropertyGroups.contains(group)) {
                result.addAll(group.getProperties());
            }
        }
        return result;
    }
}

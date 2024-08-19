package net.silentchaos512.gear.api.event;

import net.neoforged.bus.api.Event;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;

import java.util.List;

/**
 * Fired when collecting the property modifiers for a gear part. This allows modifiers to be added
 * or removed.
 */
public class GetPropertyModifiersEvent<T, V extends GearPropertyValue<T>> extends Event {
    private final PropertyKey<T, V> propertyKey;
    private final List<V> modifiers;
    private final PartInstance part;

    public GetPropertyModifiersEvent(PartInstance part, PropertyKey<T, V> property, List<V> modifiers) {
        this.propertyKey = property;
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        this.modifiers = modifiers;
        this.part = part;
    }

    public PropertyKey<T, V> getPropertyKey() {
        return propertyKey;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<V> getModifiers() {
        return modifiers;
    }

    public PartInstance getPart() {
        return part;
    }
}

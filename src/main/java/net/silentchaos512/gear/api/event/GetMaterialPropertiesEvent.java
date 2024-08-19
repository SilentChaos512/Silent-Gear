package net.silentchaos512.gear.api.event;

import net.neoforged.bus.api.Event;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fired when collecting the property modifiers for a part material. This allows modifiers to be
 * added or removed.
 *
 * @author SilentChaos512
 * @since 2.0.0
 */
public class GetMaterialPropertiesEvent extends Event {
    private final MaterialInstance material;
    private final PartType partType;
    private final GearProperty<?, ?> property;
    private final List<GearPropertyValue<?>> modifiers;

    public GetMaterialPropertiesEvent(MaterialInstance material, PartType partType, GearProperty<?, ?> property, Collection<GearPropertyValue<?>> modifiers) {
        this.property = property;
        this.partType = partType;
        this.modifiers = new ArrayList<>(modifiers);
        this.material = material;
    }

    public GearProperty<?, ?> getProperty() {
        return property;
    }

    public PartType getPartType() {
        return partType;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<GearPropertyValue<?>> getModifiers() {
        return modifiers;
    }

    public MaterialInstance getMaterial() {
        return material;
    }
}

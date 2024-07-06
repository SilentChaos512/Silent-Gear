package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.TraitInstance;

import java.util.Collection;
import java.util.List;

public class TraitListPropertyType extends GearPropertyType<List<TraitInstance>, TraitListProperty> {
    public static final Codec<TraitListProperty> CODEC = Codec.list(TraitInstance.CODEC)
            .xmap(
                    TraitListProperty::new,
                    GearProperty::value
            );

    public TraitListPropertyType(Builder<List<TraitInstance>> builder) {
        super(builder);
    }

    @Override
    public Codec<TraitListProperty> codec() {
        return CODEC;
    }

    @Override
    public List<TraitInstance> compute(List<TraitInstance> baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<TraitListProperty> modifiers) {
        return List.of();
    }
}

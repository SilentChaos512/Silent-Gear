package net.silentchaos512.gear.api.property;

import com.google.common.collect.ImmutableList;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.trait.Trait;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TraitListPropertyValue extends GearPropertyValue<List<TraitInstance>> {
    public TraitListPropertyValue(List<TraitInstance> value) {
        super(ImmutableList.copyOf(value));
    }

    public static TraitListPropertyValue empty() {
        return new TraitListPropertyValue(Collections.emptyList());
    }

    public static TraitListPropertyValue single(DataResource<Trait> trait, int level) {
        return new TraitListPropertyValue(Collections.singletonList(TraitInstance.of(trait, level)));
    }

    public static TraitListPropertyValue of(TraitInstance... traits) {
        return new TraitListPropertyValue(Arrays.stream(traits).toList());
    }

    public static TraitListPropertyValue from(Collection<TraitInstance> traits) {
        return new TraitListPropertyValue(List.copyOf(traits));
    }

    @Override
    public String toString() {
        var listText = this.value.stream()
                .map(trait -> trait.getDisplayName().toString())
                .collect(Collectors.joining(", "));
        return "[" + listText + "]";
    }
}

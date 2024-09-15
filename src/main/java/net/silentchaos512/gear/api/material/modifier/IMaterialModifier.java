package net.silentchaos512.gear.api.material.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.Collection;
import java.util.List;

public interface IMaterialModifier {
    IMaterialModifierType<?> getType();

    <T, V extends GearPropertyValue<T>> Collection<V> modifyStats(
            MaterialInstance material,
            PartType partType,
            PropertyKey<T, V> key,
            Collection<V> statMods
    );

    // TODO: modifyTraits method?

    void appendTooltip(List<Component> tooltip);

    MutableComponent modifyMaterialName(MutableComponent name);
}

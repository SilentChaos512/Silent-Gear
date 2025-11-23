package net.silentchaos512.gear.api.material.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface IMaterialModifier {
    IMaterialModifierType<?> getType();

    <T, V extends GearPropertyValue<T>> Collection<V> modifyProperties(
            MaterialInstance material,
            PartType partType,
            PropertyKey<T, V> key,
            Collection<V> mods
    );

    // TODO: modifyTraits method?

    void appendTooltip(List<Component> tooltip);

    MutableComponent modifyMaterialName(MutableComponent name);

    class Helper {
        public static <T, V extends GearPropertyValue<T>> Collection<V> modifyNumberValuesWithBonusOrPenalty(PropertyKey<T, V> key, Collection<V> mods, float bonusOrPenalty) {
            if (key.property() instanceof NumberProperty) {
                List<V> ret = new ArrayList<>();

                // Apply bonus/penalty to all modifiers. Makes it easier to see the effect on rods and such.
                for (var mod : mods) {
                    var numberValue = (NumberPropertyValue) mod;
                    float value = numberValue.value();
                    // Taking the abs of value times bonus makes negative mods become less negative
                    //noinspection unchecked
                    ret.add((V) new NumberPropertyValue(value + Math.abs(value) * bonusOrPenalty, numberValue.operation()));
                }

                return ret;
            }

            return mods;
        }
    }
}

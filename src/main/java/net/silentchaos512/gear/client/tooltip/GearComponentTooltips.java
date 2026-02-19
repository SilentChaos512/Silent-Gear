package net.silentchaos512.gear.client.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.*;

public class GearComponentTooltips {
    @SuppressWarnings("unchecked")
    public static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> Optional<MutableComponent> propertyLine(
            GearTooltipStyle format,
            FormatColorScheme valueColorScheme,
            GearType gearType,
            GearProperty<?, ?> propertyIn,
            Collection<GearPropertyValue<?>> modifiersIn
    ) {
        if (!modifiersIn.isEmpty()) {
            // Cast to true types
            var property = (P) propertyIn;
            var modifiers = (Collection<V>) modifiersIn;
            T value = property.compute(property.getZeroValue(), false, gearType, modifiers);
            boolean isZero = isTrueZeroValue(property, value, modifiers);
            if (format.showHiddenValues() || !isZero) {
                Color nameColor = isZero ? TooltipHandler.MC_DARK_GRAY : property.getGroup().getColor();

                MutableComponent nameStr = TextUtil.withOptionalColor(property.getDisplayName(), nameColor, format.colorPropertyName());
                MutableComponent statListText = GearPropertyMap.formatText(
                        modifiers,
                        property,
                        property.getPreferredDecimalPlaces(property.valueOf(value)),
                        valueColorScheme
                );

                return Optional.of(Component.translatable("property.silentgear.displayFormat", nameStr, statListText));
            }
        }

        return Optional.empty();
    }

    protected static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> Optional<MutableComponent> subPropertyLine(
            GearTooltipStyle format,
            P property,
            GearType gearType,
            Collection<V> modifiers
    ) {
        if (!modifiers.isEmpty()) {
            T value = property.compute(property.getZeroValue(), modifiers);
            boolean isZero = isTrueZeroValue(property, value, modifiers);
            if (format.showHiddenValues() || !isZero) {
                Color color = isZero ? TooltipHandler.MC_DARK_GRAY : Color.WHITE;

                MutableComponent nameStr = TextUtil.withOptionalColor(gearType.getDisplayName().copy(), color, format.colorPropertyName());
                var uncoloredFormattedText = GearPropertyMap.formatText(
                        modifiers,
                        property,
                        property.getPreferredDecimalPlaces(property.valueOf(value)),
                        FormatColorScheme.NO_COLORS
                );
                MutableComponent statListText = TextUtil.withOptionalColor(uncoloredFormattedText, color, format.colorPropertyName());

                return Optional.of(Component.translatable("property.silentgear.displayFormat", nameStr, statListText));
            }
        }

        return Optional.empty();
    }

    protected static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> boolean isTrueZeroValue(
            P property,
            T value,
            Collection<V> modifiers
    ) {
        if (!property.isZero(value)) {
            // The computed value is not zero
            return false;
        }
        for (V modifier : modifiers) {
            if (!property.isZero(modifier.value())) {
                // The modifier value is not zero (multiplier, etc.)
                return false;
            }
        }
        // The computed value and all modifiers are zero
        return true;
    }

    public static List<PartType> getSortedPartTypes(Set<PartType> set) {
        var result = new ArrayList<PartType>();
        for (var partType : SgRegistries.PART_TYPE) {
            if (set.contains(partType)) {
                result.add(partType);
            }
        }
        return result;
    }
}

package net.silentchaos512.gear.api.property;

import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.tooltip.GearTooltipStyle;
import net.silentchaos512.gear.client.util.TextListBuilder;

import java.util.Collection;

/**
 * A {@link GearProperty} that can override normal tooltip handling.
 */
public interface CustomTooltipProperty {
    /**
     * Applies custom tooltip operation to the TextListBuilder, if needed. Returning true will cancel normal tooltip
     * processing for this property.
     *
     * @param format           Tooltip style options
     * @param valueColorScheme Color scheme for values
     * @param modifiers        The property value modifiers
     * @param builder          The tooltip builder
     * @param <T>              Property value type
     * @param <V>              Property value class
     * @return True if custom processing was done and normal processing should be skipped, false otherwise
     */
    <T, V extends GearPropertyValue<T>> boolean addCustomTooltip(
            GearTooltipStyle format,
            FormatColorScheme valueColorScheme,
            Collection<V> modifiers,
            TextListBuilder builder
    );
}

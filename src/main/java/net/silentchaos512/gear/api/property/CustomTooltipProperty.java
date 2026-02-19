package net.silentchaos512.gear.api.property;

import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.tooltip.GearTooltipStyle;
import net.silentchaos512.gear.client.util.TextListBuilder;

import java.util.Collection;

public interface CustomTooltipProperty {
    <T, V extends GearPropertyValue<T>> boolean addCustomTooltip(
            GearTooltipStyle format,
            FormatColorScheme valueColorScheme,
            Collection<V> modifiers,
            TextListBuilder builder
    );
}

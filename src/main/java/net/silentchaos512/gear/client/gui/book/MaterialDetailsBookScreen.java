package net.silentchaos512.gear.client.gui.book;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class MaterialDetailsBookScreen extends AbstractMaterialBookScreen {
    public MaterialDetailsBookScreen(@Nullable Screen previousScreen, Material material) {
        super(previousScreen);
        setPages(materialSection(material));
    }

    private SectionBuilder materialSection(Material material) {
        SectionBuilder builder = new SectionBuilder();
        MaterialInstance materialInstance = MaterialInstance.of(material);

        for (PartType partType : materialInstance.getPartTypes()) {
            builder.addLabel(Component.translatable("part.silentgear.type", partType.getDisplayName()));
            builder.addEmptyLines(1);
            for (PropertyKey<?, ?> propertyKey : material.getPropertyKeys(materialInstance, partType)) {
                var propertyModifiers = new ArrayList<GearPropertyValue<?>>(materialInstance.getPropertyModifiers(partType, propertyKey));
                getStatTooltipLine(GearTypes.ALL.get(), propertyKey.property(), propertyModifiers).ifPresent(builder::addLabel);
            }
            builder.addPageBreakIfNotEmpty();
        }

        return builder;
    }

    // TODO: Copied from TooltipHandler and modified slightly. Clean up the redundancy later.
    @SuppressWarnings("unchecked")
    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> Optional<MutableComponent> getStatTooltipLine(
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
            if (!isZero) { // no advanced flags check
                Color nameColor = property.getGroup().getColor();
                Color statColor = Color.BLACK; // Black instead of white for the book

                MutableComponent nameStr = TextUtil.withColor(property.getDisplayName(), nameColor);
                var uncoloredFormattedText = GearPropertyMap.formatText(
                        modifiers,
                        property,
                        property.getPreferredDecimalPlaces(property.valueOf(value))
                );
                MutableComponent statListText = TextUtil.withColor(uncoloredFormattedText, statColor);

                return Optional.of(Component.translatable("property.silentgear.displayFormat", nameStr, statListText));
            }
        }

        return Optional.empty();
    }

    // TODO: Copied from TooltipHandler. Clean up the redundancy later.
    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> boolean isTrueZeroValue(P property, T value, Collection<V> modifiers) {
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
}

package net.silentchaos512.gear.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.CustomTooltipProperty;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.*;
import java.util.stream.Collectors;

public class MaterialTooltips extends GearComponentTooltips {
    public static void advancedInfo(List<Component> tooltip, MaterialInstance material) {
        tooltip.add(Component.literal("Material ID: " + material.getId()).withStyle(ChatFormatting.DARK_GRAY));
        var packName = SgRegistries.MATERIAL.getPackName(material.get());
        tooltip.add(Component.literal("Material data pack: " + packName).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void materialHeader(List<Component> tooltip, MaterialInstance material, boolean isPropertiesKeyHeld) {
        if (isPropertiesKeyHeld) {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.material"), Color.GOLD));
        } else {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.material"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_PROPERTIES), ChatFormatting.GRAY))));
        }
    }

    public static void materialModifierLines(List<Component> tooltip, MaterialInstance material) {
        for (IMaterialModifier modifier : material.getModifiers()) {
            modifier.appendTooltip(tooltip);
        }
    }

    public static void materialCategories(List<Component> tooltip, MaterialInstance material) {
        Collection<IMaterialCategory> categories = material.getCategories();
        if (!categories.isEmpty()) {
            Component text = TextUtil.separatedList(categories.stream().map(IMaterialCategory::getDisplayName).collect(Collectors.toList()))
                    .withStyle(ChatFormatting.ITALIC);
            tooltip.add(TextUtil.misc("materialCategories", text));
        }
    }

    public static void partTypesPagesHeader(List<Component> tooltip, Collection<PartType> types, PartType selectedType) {
        MutableComponent ret = Component.literal("| ").withStyle(ChatFormatting.GRAY);
        for (PartType type : types) {
            Color color = type == selectedType ? Color.AQUAMARINE : TooltipHandler.MC_DARK_GRAY;
            Component text = TextUtil.withColor(type.getDisplayName(), color);
            ret.append(text).append(" | ");
        }

        Component keyHint = TextUtil.misc("tooltip.material.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE));
        ret.append(keyHint);
        tooltip.add(ret);
    }

    public static void propertiesHeader(List<Component> tooltip, MaterialInstance material) {
        tooltip.add(Component.translatable("misc.silentgear.tooltip.properties").withStyle(ChatFormatting.GOLD));
    }

    public static void propertiesLines(List<Component> tooltip, GearTooltipStyle format, FormatColorScheme valueColorScheme, PartType partType, MaterialInstance material) {
        TextListBuilder builder = new TextListBuilder();

        for (GearProperty<?, ?> property : SgRegistries.GEAR_PROPERTY) {
            propertyModifierLinesForProperty(format, valueColorScheme, partType, material, builder, property);
        }

        tooltip.addAll(builder.build());
    }

    public static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> void propertyModifierLinesForProperty(
            GearTooltipStyle format,
            FormatColorScheme valueColorScheme,
            PartType partType,
            MaterialInstance material,
            TextListBuilder builder,
            P property
    ) {
        Collection<V> modsAll = material.getPropertyModifiers(partType, PropertyKey.of(property, GearTypes.ALL.get()));

        if (property instanceof CustomTooltipProperty customTooltipProperty && customTooltipProperty.addCustomTooltip(format, valueColorScheme, modsAll, builder)) {
            return;
        }

        //noinspection unchecked
        Optional<MutableComponent> head = propertyLine(format, valueColorScheme, GearTypes.ALL.get(), property, (Collection<GearPropertyValue<?>>) modsAll);
        builder.add(head.orElseGet(() -> TextUtil.withOptionalColor(property.getDisplayName(), property.getGroup().getColor(), format.colorPropertyName())));

        builder.indent();

        int subCount = 0;
        List<PropertyKey<?, ?>> keysForStat = material.get().getPropertyKeys(material, partType).stream()
                .filter(key -> key.property().equals(property))
                .toList();

        for (var key : keysForStat) {
            if (key.gearType() != GearTypes.ALL.get()) {
                //noinspection unchecked
                var castedKey = (PropertyKey<T, V>) key;
                Collection<V> mods = material.getPropertyModifiers(partType, castedKey);
                Optional<MutableComponent> line = subPropertyLine(format, castedKey.property(), key.gearType(), mods);

                if (line.isPresent()) {
                    builder.add(line.get());
                    ++subCount;
                }
            }
        }

        if (subCount == 0 && head.isEmpty()) {
            builder.removeLast();
        }

        builder.unindent();
    }

    public static void addJeiSearchTerms(List<Component> tooltip, MaterialInstance material) {
        // Add search terms to allow advanced filtering in JEI (requires the
        // `SearchAdvancedTooltips` JEI config to be set)

        StringBuilder b = new StringBuilder();

        for (IMaterialCategory category : material.getCategories()) {
            b.append(category.getName()).append(" ");
        }

        Collection<String> traits = new HashSet<>();

        for (PartType partType : material.getPartTypes()) {
            b.append(partType.getDisplayName().getString()).append(" ");
            for (TraitInstance trait : material.getTraits(PartGearKey.ofAll(partType))) {
                if (trait.isValid()) {
                    traits.add(trait.getTrait().getDisplayName(0).getString());
                }
            }
        }

        for (String str : traits) {
            b.append(str).append(" ");
        }

        tooltip.add(Component.literal(b.toString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
    }
}

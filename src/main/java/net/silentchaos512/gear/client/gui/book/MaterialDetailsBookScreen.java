package net.silentchaos512.gear.client.gui.book;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.book.page.element.LabelPageElement;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.tooltip.GearComponentTooltips;
import net.silentchaos512.gear.client.tooltip.GearTooltipStyle;
import net.silentchaos512.gear.client.tooltip.MaterialTooltips;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MaterialDetailsBookScreen extends AbstractMaterialBookScreen {
    public MaterialDetailsBookScreen(@Nullable Screen previousScreen, Material material) {
        super(previousScreen, createScreenTitle(material));
        setPages(materialSection(material));
    }

    private static MutableComponent createScreenTitle(Material material) {
        Component packName = Component.literal(String.format(" (%s)", SgRegistries.MATERIAL.getPackName(material)))
                .withStyle(ChatFormatting.GRAY);
        return material.getSimpleName().copy().withStyle(ChatFormatting.WHITE).append(packName);
    }

    private SectionBuilder materialSection(Material material) {
        SectionBuilder builder = new SectionBuilder();
        MaterialInstance materialInstance = MaterialInstance.of(material);

        addDescriptionPageIfAvailable(builder, material);

        for (PartType partType : GearComponentTooltips.getSortedPartTypes(materialInstance.getPartTypes())) {
            builder.addLabel(Component.translatable("part.silentgear.type", partType.getDisplayName()));
            builder.addEmptyLines(1);

            // No properties?
            if (material.getPropertyKeys(materialInstance, partType).isEmpty()) {
                builder.addLabel(Component.translatable("gui.silentgear.material_book.noProperties"), 0.8f);
            }

            // Additive material warning
            if (materialInstance.getProperty(partType, GearProperties.ADDITIVE.get())) {
                var labelText = Component.translatable("property.silentgear.additive.warning").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC);
                builder.addLabel(labelText, 0.6f);
                builder.addEmptyLines(1);
            }

            List<Component> propertiesLines = new ArrayList<>();
            var format = new GearTooltipStyle(false, false, false);
            MaterialTooltips.propertiesLines(propertiesLines, format, FormatColorScheme.DARK_GREY_ZERO_OR_NO_COLOR, partType, materialInstance);
            for (Component line : propertiesLines) {
                builder.add(new LabelPageElement(line, 0.6f, LabelPageElement.Alignment.LEFT, true, true));
            }

            builder.addPageBreakIfNotEmpty();
        }

        return builder;
    }

    private void addDescriptionPageIfAvailable(SectionBuilder builder, Material material) {
        Identifier id = SgRegistries.MATERIAL.getKey(material);
        String key = String.format("material.%s.%s.book_desc", id.getNamespace(), id.getPath());
        if (Language.getInstance().has(key)) {
            Component text = Component.translatable(key);
            builder.addLabel(text);
            builder.addPageBreak();
        }
    }
}

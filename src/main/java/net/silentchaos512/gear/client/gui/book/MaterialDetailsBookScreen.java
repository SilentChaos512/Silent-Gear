package net.silentchaos512.gear.client.gui.book;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.book.page.element.LabelPageElement;
import net.silentchaos512.gear.client.tooltip.MaterialTooltips;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

            List<Component> propertiesLines = new ArrayList<>();
            MaterialTooltips.propertiesLines(propertiesLines, false, false, partType, materialInstance);
            for (Component line : propertiesLines) {
                builder.add(new LabelPageElement(line, 0.6f, LabelPageElement.Alignment.LEFT));
            }

            builder.addPageBreakIfNotEmpty();
        }

        return builder;
    }
}

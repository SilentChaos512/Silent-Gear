package net.silentchaos512.gear.client.gui.book;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.book.page.element.ClickableLabelPageElement;
import net.silentchaos512.gear.client.gui.book.page.element.EmptyPageElement;
import net.silentchaos512.gear.client.gui.book.page.element.LabelPageElement;
import net.silentchaos512.gear.client.gui.book.page.element.PageElement;
import net.silentchaos512.gear.client.gui.component.LabelButton;
import net.silentchaos512.gear.setup.SgRegistries;

public class MaterialBookScreen extends AbstractMaterialBookScreen {
    public MaterialBookScreen() {
        super(null);
        setPages(homeSection());
    }

    private SectionBuilder homeSection() {
        var font = Minecraft.getInstance().font;
        SectionBuilder builder = new SectionBuilder();
        // Title page
        var labelHeight = font.lineHeight + PageElement.VERTICAL_PADDING;
        builder.add(new EmptyPageElement(Page.PAGE_HEIGHT / 4));
        builder.add(new LabelPageElement(Component.translatable("gui.silentgear.material_book.title")));
        builder.add(new EmptyPageElement(2 * labelHeight));
        builder.add(new LabelPageElement(Component.translatable("gui.silentgear.material_book.title2"), 0.7f));
        builder.add(new EmptyPageElement(labelHeight));
        builder.add(new LabelPageElement(Component.literal("This book is a work in progress. Some elements may change.")));
        builder.addPageBreak();
        // Sort and display options
        builder.add(new ClickableLabelPageElement(Component.translatable("misc.silentgear.allMaterials.byName"), this::onPressAllMaterialsByName));
        builder.add(new ClickableLabelPageElement(Component.translatable("misc.silentgear.allMaterials.byId"), this::onPressAllMaterialsById));
        return builder;
    }

    private void onPressAllMaterialsByName(LabelButton button) {
        var newScreen = new MaterialListBookScreen(this, SgRegistries.MATERIAL.getValues(true), MaterialListBookScreen.MATERIAL_SORT_BY_DISPLAY_NAME);
        Minecraft.getInstance().setScreen(newScreen);
    }

    private void onPressAllMaterialsById(LabelButton button) {
        var newScreen = new MaterialListBookScreen(this, SgRegistries.MATERIAL.getValues(true), MaterialListBookScreen.MATERIAL_SORT_BY_ID);
        Minecraft.getInstance().setScreen(newScreen);
    }
}

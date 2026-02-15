package net.silentchaos512.gear.client.gui.book;

import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.book.page.element.ClickableLabelPageElement;
import net.silentchaos512.gear.client.gui.book.page.element.LabelPageElement;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.ArrayList;

public class MaterialBookScreen extends AbstractMaterialBookScreen {
    public MaterialBookScreen() {
        super(null, GameNarrator.NO_TITLE);
        setPages(homeSection());
    }

    private SectionBuilder homeSection() {
        SectionBuilder builder = new SectionBuilder();

        // Title page
        builder.addEmpty(Page.PAGE_HEIGHT / 4);
        builder.add(new LabelPageElement(Component.translatable("gui.silentgear.material_book.title"), 1.25f, LabelPageElement.Alignment.LEFT));
        builder.addEmptyLines(1);
        builder.add(new LabelPageElement(Component.translatable("gui.silentgear.material_book.title2"), 0.7f, LabelPageElement.Alignment.LEFT));
        builder.addEmptyLines(1);
        builder.addLabel(Component.literal("This book is a work in progress. Some elements may change.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
        builder.addPageBreak();

        // Sort and display options
        builder.addLabel(Component.literal("• ").append(Component.translatable("gui.silentgear.material_book.allMaterials")));
        var byNameTitle = Component.literal("  ◦ ").append(Component.translatable("gui.silentgear.material_book.byName"));
        builder.add(new ClickableLabelPageElement(byNameTitle, button -> onPressAllMaterialsByName(byNameTitle)));
        var byIdTitle = Component.literal("  ◦ ").append(Component.translatable("gui.silentgear.material_book.byId"));
        builder.add(new ClickableLabelPageElement(byIdTitle, button -> onPressAllMaterialsById(byIdTitle)));
        builder.addLabel(Component.literal("• ").append(Component.translatable("gui.silentgear.material_book.byProperty")));
        for (GearProperty<?, ? extends GearPropertyValue<?>> property : SgRegistries.GEAR_PROPERTY) {
            if (property instanceof NumberProperty numberProperty) {
                var title = Component.literal("  ◦ ").append(property.getDisplayName());
                builder.add(new ClickableLabelPageElement(title, button -> onPressAllMaterialsByProperty(title, numberProperty)));
            }
        }

        return builder;
    }

    private void onPressAllMaterialsByName(Component newScreenTitle) {
        var materials = SgRegistries.MATERIAL.getValues(true);
        var newScreen = new MaterialListBookScreen(this, newScreenTitle, materials, MaterialListBookScreen.MATERIAL_SORT_BY_DISPLAY_NAME);
        Minecraft.getInstance().setScreen(newScreen);
    }

    private void onPressAllMaterialsById(Component newScreenTitle) {
        var materials = SgRegistries.MATERIAL.getValues(true);
        var newScreen = new MaterialListBookScreen(this, newScreenTitle, materials, MaterialListBookScreen.MATERIAL_SORT_BY_ID);
        Minecraft.getInstance().setScreen(newScreen);
    }

    private void onPressAllMaterialsByProperty(Component newScreenTitle, GearProperty<Float, NumberPropertyValue> property) {
        var materials = new ArrayList<>(SgRegistries.MATERIAL.getValues(true));
        materials.removeIf(mat -> mat.getProperty(MaterialInstance.of(mat), PartTypes.MAIN, PropertyKey.of(property, GearTypes.ALL.get())) <= 0.0001f);
        var newScreen = new MaterialListBookScreen(this, newScreenTitle, materials, MaterialListBookScreen.compareByProperty(property));
        Minecraft.getInstance().setScreen(newScreen);
    }
}

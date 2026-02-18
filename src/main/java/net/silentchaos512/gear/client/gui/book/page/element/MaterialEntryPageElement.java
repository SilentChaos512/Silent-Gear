package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.book.MaterialDetailsBookScreen;
import net.silentchaos512.gear.client.gui.component.IngredientLabelButton;

public record MaterialEntryPageElement(Material material) implements PageElement {
    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        Component text = material.getSimpleName();
        var widgetWidth = IngredientLabelButton.getWidth(text, font);
        var widgetHeight = font.lineHeight + VERTICAL_PADDING;
        componentAccess.addRenderableWidget(
                new IngredientLabelButton(
                        pageX, pageY, widgetWidth, widgetHeight,
                        material,
                        text,
                        font,
                        button -> onPress(componentAccess, material)
                )
        );
    }

    @Override
    public int getHeight(Font font) {
        return font.lineHeight + VERTICAL_PADDING;
    }

    private void onPress(AbstractMaterialBookScreen.ComponentAccess componentAccess, Material material) {
        var screen = new MaterialDetailsBookScreen(componentAccess.screen(), material);
        var minecraft = Minecraft.getInstance();
        minecraft.execute(() -> minecraft.setScreen(screen));
    }
}

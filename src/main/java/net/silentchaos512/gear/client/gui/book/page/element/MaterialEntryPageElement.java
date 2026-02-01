package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.component.IngredientLabelButton;
import net.silentchaos512.gear.client.gui.component.LabelButton;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;

public record MaterialEntryPageElement(Material material) implements PageElement {
    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        var text = material.getDisplayName(MaterialInstance.of(material), PartTypes.MAIN.get());
        componentAccess.addRenderableWidget(
                new IngredientLabelButton(
                        pageX, pageY, IngredientLabelButton.getWidth(text, font), font.lineHeight + VERTICAL_PADDING,
                        material.getIngredient(),
                        text,
                        font,
                        this::onPress
                )
        );
    }

    @Override
    public int getHeight(Font font) {
        return font.lineHeight + VERTICAL_PADDING;
    }

    private void onPress(LabelButton labelButton) {
        // TODO: Open material section
    }
}

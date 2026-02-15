package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.component.LabelButton;

public record ClickableLabelPageElement(Component text, LabelButton.OnPress onPress) implements PageElement {
    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        componentAccess.addRenderableWidget(
                new LabelButton(
                        pageX, pageY, font.width(this.text.getVisualOrderText()), font.lineHeight + VERTICAL_PADDING,
                        this.text,
                        font,
                        this.onPress
                )
        );
    }

    @Override
    public int getHeight(Font font) {
        return font.lineHeight + VERTICAL_PADDING;
    }
}

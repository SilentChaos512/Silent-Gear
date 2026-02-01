package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.component.LabelWidget;

public record LabelPageElement(Component text, float scale) implements PageElement {
    public LabelPageElement(Component text) {
        this(text, 1.0f);
    }

    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        var widget = componentAccess.addRenderableOnly(
                new LabelWidget(
                        pageX, pageY, Page.PAGE_WIDTH, Math.round((font.lineHeight + VERTICAL_PADDING) / this.scale),
                        this.text,
                        font
                )
        );
        widget.setScale(this.scale);
        widget.alignCenter();
        widget.setColor(0x0);
    }

    @Override
    public int getHeight(Font font) {
        return Math.round((font.lineHeight + VERTICAL_PADDING) / this.scale);
    }
}

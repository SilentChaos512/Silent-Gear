package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.component.LabelWidget;

public record LabelPageElement(Component text, float scale, Alignment alignment) implements PageElement {
    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public LabelPageElement(Component text) {
        this(text, 1.0f, Alignment.CENTER);
    }

    public LabelPageElement(Component text, float scale) {
        this(text, scale, Alignment.CENTER);
    }

    public LabelPageElement(Component text, Alignment alignment) {
        this(text, 1.0f, alignment);
    }

    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        int scaledHeight = Math.round((font.lineHeight + VERTICAL_PADDING) * this.scale);
        int scaledWidth = Math.round(Page.PAGE_WIDTH / this.scale);
        var widget = componentAccess.addRenderableOnly(
                new LabelWidget(
                        pageX, pageY, scaledWidth, scaledHeight,
                        this.text,
                        font
                )
        );
        widget.setScale(this.scale);
        switch (alignment) {
            case LEFT -> widget.alignLeft();
            case CENTER -> widget.alignCenter();
            case RIGHT -> widget.alignRight();
        }
        widget.setColor(0x0);
    }

    @Override
    public int getHeight(Font font) {
        return Math.round((font.lineHeight + VERTICAL_PADDING) * this.scale);
    }
}

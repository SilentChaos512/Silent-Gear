package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.component.LabelWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record LabelPageElement(
        Component text,
        float scale,
        Alignment alignment,
        boolean autoWrap,
        boolean indentWrap
) implements PageElement {
    public enum Alignment {
        LEFT, CENTER, RIGHT;
    }

    public LabelPageElement(Component text) {
        this(text, 1.0f, Alignment.CENTER, true, false);
    }

    public LabelPageElement(Component text, float scale) {
        this(text, scale, Alignment.CENTER, true, false);
    }

    public LabelPageElement(Component text, Alignment alignment) {
        this(text, 1.0f, alignment, true, false);
    }

    public LabelPageElement(Component line, float scale, Alignment alignment) {
        this(line, scale, alignment, true, false);
    }

    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        int scaledHeight = Math.round((font.lineHeight + VERTICAL_PADDING) * this.scale);
        int scaledWidth = Math.round(Page.PAGE_WIDTH / this.scale);
        int y = pageY;
        for (Component line : splitLines(font, this.text)) {
            var widget = componentAccess.addRenderableOnly(
                    new LabelWidget(pageX, y, scaledWidth, scaledHeight, line, font)
            );
            widget.setScale(this.scale);
            switch (alignment) {
                case LEFT -> widget.alignLeft();
                case CENTER -> widget.alignCenter();
                case RIGHT -> widget.alignRight();
            }
            y += scaledHeight;
        }
    }

    @Override
    public int getHeight(Font font) {
        int singleHeight = Math.round((font.lineHeight + VERTICAL_PADDING) * this.scale);
        if (!this.autoWrap) {
            return singleHeight;
        }
        int lines = splitLines(font, this.text).size();
        return lines * singleHeight;
    }

    private List<Component> splitLines(Font font, Component fullText) {
        final int maxWidth = (int) (Page.PAGE_WIDTH / this.scale);

        if (!this.autoWrap || font.width(fullText.getVisualOrderText()) < maxWidth) {
            return Collections.singletonList(fullText);
        }

        List<Component> result = new ArrayList<>();
        // TODO: Split hyphenated words too?
        String[] words = fullText.getString().replaceAll("(§.)+", "").split("\\s+");
        String lineBuilder = "";
        String line;
        for (String word : words) {
            line = lineBuilder;
            if (!lineBuilder.isEmpty()) {
                lineBuilder += " ";
            }
            lineBuilder = lineBuilder + word;
            if (font.width(lineBuilder) > maxWidth) {
                result.add(Component.literal(line).withStyle(fullText.getStyle()));
                lineBuilder = word;
                if (this.indentWrap) {
                    lineBuilder = "    " + lineBuilder;
                }
            }
        }
        if (!lineBuilder.isEmpty()) {
            result.add(Component.literal(lineBuilder).withStyle(fullText.getStyle()));
        }
        return result;
    }
}

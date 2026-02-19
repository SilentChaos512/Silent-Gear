package net.silentchaos512.gear.client.gui.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.silentchaos512.lib.util.MathUtils;

public class LabelWidget extends StringWidget {
    protected float alignX = 0.0F;
    // FIXME: scaling does not work correctly when text is centered
    protected float scale = 1.0F;

    public LabelWidget(Component message, Font font) {
        this(0, 0, font.width(message.getVisualOrderText()), font.lineHeight, message, font);
    }

    public LabelWidget(int width, int height, Component message, Font font) {
        this(0, 0, width, height, message, font);
    }

    public LabelWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message, font);
    }

    public LabelWidget setScale(float scale) {
        if (MathUtils.floatsEqual(scale, 0f)) {
            throw new IllegalArgumentException("widget scale cannot be zero");
        }
        this.scale = scale;
        return this;
    }

    private LabelWidget horizontalAlignment(float horizontalAlignment) {
        this.alignX = horizontalAlignment;
        return this;
    }

    public LabelWidget alignLeft() {
        return this.horizontalAlignment(0.0F);
    }

    public LabelWidget alignCenter() {
        return this.horizontalAlignment(0.5F);
    }

    public LabelWidget alignRight() {
        return this.horizontalAlignment(1.0F);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderWithHorizontalOffset(guiGraphics, 0);
    }

    protected void renderWithHorizontalOffset(GuiGraphics guiGraphics, int xOffset) {
        var component = this.getMessage().copy();
        if (this.active && this.isHovered) {
            component = this.getMessage().copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GOLD);
        }
        Font font = this.getFont();
        int width = this.getWidth();
        int textWidth = font.width(component);
        var rawAlignmentX = Math.round(this.alignX * (width - textWidth) / scale);
        int rawX = this.getX() + rawAlignmentX + xOffset;
        int rawY = this.getY() + (this.getHeight() - 9) / 2;
        int scaledX = Math.round(rawX / this.scale);
        int scaledY = Math.round(rawY / this.scale);
        FormattedCharSequence formattedcharsequence = textWidth > width ? this.clipText(component, width) : component.getVisualOrderText();
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(this.scale, this.scale);
        guiGraphics.drawString(font, formattedcharsequence, scaledX, scaledY, 0xFF000000, false);
        guiGraphics.pose().popMatrix();
    }

    protected FormattedCharSequence clipText(Component message, int width) {
        Font font = this.getFont();
        FormattedText formattedtext = font.substrByWidth(message, width - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(formattedtext, CommonComponents.ELLIPSIS));
    }
}

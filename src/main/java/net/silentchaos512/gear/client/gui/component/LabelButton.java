package net.silentchaos512.gear.client.gui.component;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class LabelButton extends StringWidget {
    private final LabelButton.OnPress onPress;
    protected float alignX = 0.0F;

    public LabelButton(Component message, Font font, LabelButton.OnPress onPress) {
        this(0, 0, font.width(message.getVisualOrderText()), 9, message, font, onPress);
    }

    public LabelButton(int width, int height, Component message, Font font, LabelButton.OnPress onPress) {
        this(0, 0, width, height, message, font, onPress);
    }

    public LabelButton(int x, int y, int width, int height, Component message, Font font, LabelButton.OnPress onPress) {
        super(x, y, width, height, message, font);
        this.onPress = onPress;
        this.setColor(0x0);
        this.active = true;
    }

    private LabelButton horizontalAlignment(float horizontalAlignment) {
        this.alignX = horizontalAlignment;
        return this;
    }

    public LabelButton alignLeft() {
        return this.horizontalAlignment(0.0F);
    }

    public LabelButton alignCenter() {
        return this.horizontalAlignment(0.5F);
    }

    public LabelButton alignRight() {
        return this.horizontalAlignment(1.0F);
    }

    private void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (CommonInputs.selected(keyCode)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderWithHorizontalOffset(guiGraphics, 0);
    }

    protected void renderWithHorizontalOffset(GuiGraphics guiGraphics, int xOffset) {
        var component = this.getMessage().copy();
        if (this.isHovered) {
            component = component.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GOLD);
        }
        Font font = this.getFont();
        int width = this.getWidth();
        int textWidth = font.width(component);
        int x = this.getX() + Math.round(this.alignX * (float)(width - textWidth)) + xOffset;
        int y = this.getY() + (this.getHeight() - 9) / 2;
        FormattedCharSequence formattedcharsequence = textWidth > width ? this.clipText(component, width) : component.getVisualOrderText();
        guiGraphics.drawString(font, formattedcharsequence, x, y, this.getColor(), false);
    }

    protected FormattedCharSequence clipText(Component message, int width) {
        Font font = this.getFont();
        FormattedText formattedtext = font.substrByWidth(message, width - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(formattedtext, CommonComponents.ELLIPSIS));
    }

    public interface OnPress {
        void onPress(LabelButton button);
    }
}

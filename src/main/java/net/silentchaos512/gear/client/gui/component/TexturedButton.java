package net.silentchaos512.gear.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class TexturedButton extends Button {
    private final WidgetSprites sprites;
    private final boolean spriteBlit; // since blitSprite isn't working for mod textures...

    public TexturedButton(boolean spriteBlit, int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, GameNarrator.NO_TITLE, onPress, messageSupplier -> GameNarrator.NO_TITLE.copy());
        this.sprites = sprites;
        this.spriteBlit = spriteBlit;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        ResourceLocation sprite = this.sprites.get(this.active, this.isHoveredOrFocused());
        if (this.spriteBlit) {
            guiGraphics.blitSprite(sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        } else {
            guiGraphics.blit(sprite, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

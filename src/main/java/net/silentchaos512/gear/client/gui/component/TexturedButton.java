package net.silentchaos512.gear.client.gui.component;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TexturedButton extends Button {
    private final WidgetSprites sprites;
    private final boolean spriteBlit; // since blitSprite isn't working for mod textures...

    public TexturedButton(boolean spriteBlit, int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, GameNarrator.NO_TITLE, onPress, messageSupplier -> GameNarrator.NO_TITLE.copy());
        this.sprites = sprites;
        this.spriteBlit = spriteBlit;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Identifier sprite = this.sprites.get(this.active, this.isHoveredOrFocused());
        int width = this.getWidth();
        int height = this.getHeight();
        int x = this.getX();
        int y = this.getY();
        if (this.spriteBlit) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, width, height, 0, 0, x, y, width, height);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, sprite, x, y, 0, 0, width, height, width, height);
        }
    }
}

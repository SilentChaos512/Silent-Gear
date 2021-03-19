package net.silentchaos512.gear.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;

public class GuideBookScreen extends Screen {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/guide_book.png");

    public GuideBookScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    @Override
    protected void init() {
        super.init();
        // buttons, links?
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - 176) / 2;
        int posY = (this.height - 166) / 2;
        blit(matrixStack, posX, posY, 0, 0, 176, 166);
    }
}

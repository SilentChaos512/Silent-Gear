package net.silentchaos512.gear.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TexturedButton extends GuiButton {
    public List<String> textList = new ArrayList<>();
    protected final ResourceLocation resLoc;
    protected int texturePosX;
    protected int texturePosY;

    public TexturedButton(ResourceLocation resLoc, int id, int x, int y, int texturePosX, int texturePosY, int width, int height) {
        this(resLoc, id, x, y, texturePosX, texturePosY, width, height, new ArrayList<>());
    }

    public TexturedButton(ResourceLocation resLoc, int id, int x, int y, int texturePosX, int texturePosY, int width, int height, List<String> hoverTextList) {
        super(id, x, y, width, height, "");
        this.texturePosX = texturePosX;
        this.texturePosY = texturePosY;
        this.resLoc = resLoc;
        this.textList.addAll(hoverTextList);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(this.resLoc);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
                    && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            if (k == 0) {
                k = 1;
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.x, this.y, this.texturePosX,
                    this.texturePosY - this.height + k * this.height, this.width, this.height);
            // FIXME: What is this?
//            this.mouseDragged(minecraft, mouseX, mouseY);
        }
    }

    public void drawHover(int x, int y) {
        if (this.isMouseOver()) {
            Minecraft mc = Minecraft.getInstance();
            GuiUtils.drawHoveringText(
                    this.textList,
                    x,
                    y,
                    mc.mainWindow.getWidth(),
                    mc.mainWindow.getHeight(),
                    -1,
                    mc.fontRenderer);
        }
    }
}

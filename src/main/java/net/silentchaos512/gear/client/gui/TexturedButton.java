package net.silentchaos512.gear.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TexturedButton extends Button {
    public List<String> textList = new ArrayList<>();
    protected final ResourceLocation resLoc;
    protected int texturePosX;
    protected int texturePosY;

    public TexturedButton(ResourceLocation resLoc, int x, int y, int texturePosX, int texturePosY, int width, int height, IPressable action) {
        this(resLoc, x, y, texturePosX, texturePosY, width, height, new ArrayList<>(), action);
    }

    public TexturedButton(ResourceLocation resLoc, int x, int y, int texturePosX, int texturePosY, int width, int height, List<String> hoverTextList, IPressable action) {
        super(x, y, width, height, "", action);
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
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered());
            if (k == 0) {
                k = 1;
            }

            GlStateManager.disableDepthTest();
            this.blit(
                    this.x,
                    this.y,
                    this.texturePosX,
                    this.texturePosY - this.height + k * this.height,
                    this.width,
                    this.height);
            GlStateManager.enableDepthTest();
        }
    }

    public void drawHover(int x, int y) {
        if (this.isMouseOver(x, y)) {
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

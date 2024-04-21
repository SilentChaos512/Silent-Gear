package net.silentchaos512.gear.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TexturedButton extends Button {
    public List<String> textList = new ArrayList<>();
    protected final ResourceLocation resLoc;
    protected int texturePosX;
    protected int texturePosY;

    public TexturedButton(ResourceLocation resLoc, int x, int y, int texturePosX, int texturePosY, int width, int height, OnPress action) {
        this(resLoc, x, y, texturePosX, texturePosY, width, height, new ArrayList<>(), action);
    }

    public TexturedButton(ResourceLocation resLoc, int x, int y, int texturePosX, int texturePosY, int width, int height, List<String> hoverTextList, OnPress action) {
        super(x, y, width, height, Component.literal(""), action, DEFAULT_NARRATION);
        this.texturePosX = texturePosX;
        this.texturePosY = texturePosY;
        this.resLoc = resLoc;
        this.textList.addAll(hoverTextList);
    }

    /*@Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(this.resLoc);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered());
            if (k == 0) {
                k = 1;
            }

            RenderSystem.disableDepthTest();
            this.blit(
                    this.x,
                    this.y,
                    this.texturePosX,
                    this.texturePosY - this.height + k * this.height,
                    this.width,
                    this.height);
            RenderSystem.enableDepthTest();
        }
    }

    public void drawHover(int x, int y) {
        if (this.isMouseOver(x, y)) {
            Minecraft mc = Minecraft.getInstance();
            MainWindow mainWindow = mc.getMainWindow();
            GuiUtils.drawHoveringText(
                    this.textList,
                    x,
                    y,
                    mainWindow.getWidth(),
                    mainWindow.getHeight(),
                    -1,
                    mc.fontRenderer);
        }
    }*/
}

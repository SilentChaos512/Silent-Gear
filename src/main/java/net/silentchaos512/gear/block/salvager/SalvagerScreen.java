package net.silentchaos512.gear.block.salvager;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.SilentGear;

public class SalvagerScreen extends AbstractContainerScreen<SalvagerContainer> {
    public static final Identifier TEXTURE = SilentGear.getId("textures/gui/salvager.png");

    public SalvagerScreen(SalvagerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Progress arrow
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX + 32, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16, 256, 256);
    }
}

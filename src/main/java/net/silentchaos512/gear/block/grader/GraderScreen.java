package net.silentchaos512.gear.block.grader;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.setup.SgBlocks;

public class GraderScreen extends AbstractContainerScreen<GraderContainer> {
    public static final Identifier TEXTURE = SilentGear.getId("textures/gui/grader.png");

    public GraderScreen(GraderContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        Component text = SgBlocks.MATERIAL_GRADER.get().getName();
        graphics.text(this.font, text.getString(), 28, 6, 0x404040, false);

        MaterialGrade lastAttempt = this.menu.getLastGradeAttempt();
        if (lastAttempt != MaterialGrade.NONE) {
            graphics.text(this.font, lastAttempt.getDisplayName().getVisualOrderText(), 50, 55, 0xFFFFFF, true);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Progress arrow
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX + 49, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16, 256, 256);
    }
}

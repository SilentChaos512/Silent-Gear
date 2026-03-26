package net.silentchaos512.gear.block.alloymaker;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;

public abstract class AlloyMakerScreen extends AbstractContainerScreen<AlloyMakerContainer> {
    private Button workButton;
    private boolean lastWorkEnabledValue;

    public AlloyMakerScreen(AlloyMakerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    public abstract Identifier getTexture();

    @Override
    protected void init() {
        super.init();
        this.lastWorkEnabledValue = this.menu.getWorkEnabled();
        this.workButton = Button.builder(getWorkEnabledButtonTitle(), b -> {
            this.menu.toggleWorkEnabled();
            b.setMessage(getWorkEnabledButtonTitle());
        }).bounds(this.leftPos + 70, this.topPos + 60, 50, 20).build();;
        this.addWidget(this.workButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (this.menu.getWorkEnabled() != lastWorkEnabledValue) {
            lastWorkEnabledValue = this.menu.getWorkEnabled();
            this.workButton.setMessage(getWorkEnabledButtonTitle());
        }
    }

    @Nonnull
    private Component getWorkEnabledButtonTitle() {
        Component text = TextUtil.misc(this.menu.getWorkEnabled() ? "on" : "off");
        return TextUtil.translate("block", "alloy_maker.workEnabled", text);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.workButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        Identifier texture = getTexture();
        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, posX, posY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Progress arrow
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, posX + 93, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16, 256, 256);
    }
}

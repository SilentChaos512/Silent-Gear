package net.silentchaos512.gear.block.paintmixer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

public class PaintMixerScreen extends AbstractContainerScreen<PaintMixerMenu> {
    public static final Identifier TEXTURE = SilentGear.getId("textures/gui/paint_mixer.png");

    private Button workButton;
    private boolean lastWorkEnabledValue;

    public PaintMixerScreen(PaintMixerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.lastWorkEnabledValue = this.menu.getWorkEnabled();
        this.workButton = Button.builder(getWorkEnabledButtonTitle(), b -> {
            this.menu.toggleWorkEnabled();
            b.setMessage(getWorkEnabledButtonTitle());
        }).bounds(this.leftPos + 100, this.topPos + 62, 50, 20).build();;
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

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Progress arrow
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX + 93, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16, 256, 256);

        // Color indicator
        renderColorSample(graphics);
    }

    private void renderColorSample(GuiGraphicsExtractor graphics) {
        var left = this.leftPos + 16;
        var top = this.topPos + 61;
        var bottom = top + 11;
        var color = this.menu.getPaintColor() | 0xFF000000;
        graphics.fill(left + 16, top, left + 56, bottom, color);
        graphics.fill(left, top, left + 8, bottom, Color.blend(color, 0xFF000000, 0.75f));
        graphics.fill(left + 8, top, left + 16, bottom, Color.blend(color, 0xFF000000, 0.5f));
        graphics.fill(left + 56, top, left + 64, bottom, Color.blend(color, -1, 0.5f));
        graphics.fill(left + 64, top, left + 72, bottom, Color.blend(color, -1, 0.75f));
    }
}

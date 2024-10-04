package net.silentchaos512.gear.block.alloymaker;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;

public abstract class AlloyMakerScreen extends AbstractContainerScreen<AlloyMakerContainer> {
    private Button workButton;
    private boolean lastWorkEnabledValue;

    public AlloyMakerScreen(AlloyMakerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    public abstract ResourceLocation getTexture();

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
        return TextUtil.translate("block", "compounder.workEnabled", text);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.workButton.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        ResourceLocation texture = getTexture();

        RenderSystem.clearColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(texture, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        // Progress arrow
        graphics.blit(texture, posX + 93, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16);
    }
}

package net.silentchaos512.gear.block.compounder;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;

public class CompounderScreen extends ContainerScreen<CompounderContainer> {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/compounder.png");

    private Button workButton;
    private boolean lastWorkEnabledValue;

    public CompounderScreen(CompounderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        this.lastWorkEnabledValue = this.menu.getWorkEnabled();
        this.workButton = new Button(this.leftPos + 70, this.topPos + 60, 50, 20, getWorkEnabledButtonTitle(), b -> {
            this.menu.toggleWorkEnabled();
            b.setMessage(getWorkEnabledButtonTitle());
        });
        this.addButton(this.workButton);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.menu.getWorkEnabled() != lastWorkEnabledValue) {
            lastWorkEnabledValue = this.menu.getWorkEnabled();
            this.workButton.setMessage(getWorkEnabledButtonTitle());
        }
    }

    @Nonnull
    private ITextComponent getWorkEnabledButtonTitle() {
        ITextComponent text = TextUtil.misc(this.menu.getWorkEnabled() ? "on" : "off");
        return TextUtil.translate("block", "compounder.workEnabled", text);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bind(TEXTURE);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        // Progress arrow
        blit(matrixStack, posX + 93, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16);
    }
}

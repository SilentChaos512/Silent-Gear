package net.silentchaos512.gear.block.charger;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public class ChargerScreen extends ContainerScreen<ChargerContainer> {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/charger.png");

    public ChargerScreen(ChargerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int x, int y) {
        if (isHovering(153, 17, 13, 51, x, y)) {
            List<ITextComponent> text = ImmutableList.of(
                    TextUtil.translate("container", "material_charger.charge",
                            String.format("%,d", menu.getCharge()),
                            String.format("%,d", menu.getMaxCharge())),
                    TextUtil.translate("container", "material_charger.charge.hint")
            );
            renderComponentTooltip(matrixStack, text, x, y);
        }
        super.renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        ITextComponent text = TextUtil.translate("container", "material_charger");
        font.draw(matrixStack, text.getString(), 28, 6, 0x404040);
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
        blit(matrixStack, posX + 79, posY + 35, 176, 14, menu.getProgressArrowScale() + 1, 16);

        // Charge meter
        int chargeMeterHeight = menu.getChargeMeterHeight();
        if (chargeMeterHeight > 0) {
            blit(matrixStack, posX + 154, posY + 68 - chargeMeterHeight, 176, 31, 12, chargeMeterHeight);
        }
    }
}

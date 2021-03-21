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
        this.renderHoveredTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
        if (isPointInRegion(153, 17, 13, 51, x, y)) {
            List<ITextComponent> text = ImmutableList.of(
                    TextUtil.translate("container", "material_charger.charge",
                            String.format("%,d", container.getCharge()),
                            String.format("%,d", container.getMaxCharge())),
                    TextUtil.translate("container", "material_charger.charge.hint")
            );
            func_243308_b(matrixStack, text, x, y);
        }
        super.renderHoveredTooltip(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        ITextComponent text = TextUtil.translate("container", "material_charger");
        font.drawString(matrixStack, text.getString(), 28, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        blit(matrixStack, posX, posY, 0, 0, this.xSize, this.ySize);

        // Progress arrow
        blit(matrixStack, posX + 79, posY + 35, 176, 14, container.getProgressArrowScale() + 1, 16);

        // Charge meter
        int chargeMeterHeight = container.getChargeMeterHeight();
        if (chargeMeterHeight > 0) {
            blit(matrixStack, posX + 154, posY + 68 - chargeMeterHeight, 176, 31, 12, chargeMeterHeight);
        }
    }
}

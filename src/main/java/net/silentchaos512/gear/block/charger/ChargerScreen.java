package net.silentchaos512.gear.block.charger;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public class ChargerScreen extends AbstractContainerScreen<ChargerContainer> {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/charger.png");

    public ChargerScreen(ChargerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int x, int y) {
        if (isHovering(153, 17, 13, 51, x, y)) {
            List<Component> text = ImmutableList.of(
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
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        Component text = TextUtil.translate("container", "material_charger");
        font.draw(matrixStack, text.getString(), 28, 6, 0x404040);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.clearColor(1, 1, 1, 1);
        minecraft.getTextureManager().getTexture(TEXTURE);

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

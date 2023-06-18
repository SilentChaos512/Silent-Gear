package net.silentchaos512.gear.block.charger;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;
import java.util.Optional;

public class ChargerScreen extends AbstractContainerScreen<ChargerContainer> {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/charger.png");

    public ChargerScreen(ChargerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y) {
        if (isHovering(153, 17, 13, 51, x, y)) {
            List<Component> text = ImmutableList.of(
                    TextUtil.translate("container", "material_charger.charge",
                            String.format("%,d", menu.getCharge()),
                            String.format("%,d", menu.getMaxCharge())),
                    TextUtil.translate("container", "material_charger.charge.hint")
            );
            graphics.renderTooltip(this.font, text, Optional.empty(), x, y);
        }
        super.renderTooltip(graphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int x, int y) {
        Component text = TextUtil.translate("container", "material_charger");
        graphics.drawString(this.font, text.getString(), 28, 6, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.clearColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        // Progress arrow
        graphics.blit(TEXTURE, posX + 79, posY + 35, 176, 14, menu.getProgressArrowScale() + 1, 16);

        // Charge meter
        int chargeMeterHeight = menu.getChargeMeterHeight();
        if (chargeMeterHeight > 0) {
            graphics.blit(TEXTURE, posX + 154, posY + 68 - chargeMeterHeight, 176, 31, 12, chargeMeterHeight);
        }
    }
}

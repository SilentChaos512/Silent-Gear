package net.silentchaos512.gear.block.charger;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;
import java.util.Optional;

public class ChargerContainerScreen extends AbstractContainerScreen<ChargerContainerMenu> {
    public static final Identifier TEXTURE = SilentGear.getId("textures/gui/charger.png");

    public ChargerContainerScreen(ChargerContainerMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isHovering(153, 17, 13, 51, mouseX, mouseY)) {
            List<Component> text = ImmutableList.of(
                    TextUtil.translate("container", "material_charger.charge",
                            String.format("%,d", menu.getCharge()),
                            String.format("%,d", menu.getMaxCharge())),
                    TextUtil.translate("container", "material_charger.charge.hint")
            );
            graphics.setTooltipForNextFrame(this.font, text, Optional.empty(), mouseX, mouseY);
        } else if (isHovering(8, 70, 100, 8, mouseX, mouseY)) {
            var text = TextUtil.translate("container", "material_charger.structure_level.hint");
            graphics.setTooltipForNextFrame(this.font, List.of(text), Optional.empty(), mouseX, mouseY);
        }
        super.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        Component text = TextUtil.translate("container", "material_charger");
        graphics.text(this.font, text.getString(), 8, 6, 0x404040, false);
        var structureLevel = this.menu.fields.get(2);
        var structureText = TextUtil.translate("container", "material_charger.structure_level", structureLevel);
        graphics.text(this.font, structureText.getString(), 8, 70, 0x404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX, posY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Progress arrow
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX + 79, posY + 35, 176, 14, menu.getProgressArrowScale() + 1, 16, 256, 256);

        // Charge meter
        int chargeMeterHeight = menu.getChargeMeterHeight();
        if (chargeMeterHeight > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, posX + 154, posY + 68 - chargeMeterHeight, 176, 31, 12, chargeMeterHeight, 256, 256);
        }
    }
}

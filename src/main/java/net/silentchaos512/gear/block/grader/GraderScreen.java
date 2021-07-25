package net.silentchaos512.gear.block.grader;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.init.ModBlocks;

public class GraderScreen extends AbstractContainerScreen<GraderContainer> {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/grader.png");

    public GraderScreen(GraderContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

/*    @Override
    public List<String> getTooltipFromItem(ItemStack stack) {
        List<String> list = super.getTooltipFromItem(stack);
        // Add catalyst tier to tooltip, only in part analyzer
        int catalystTier = GraderTileEntity.getCatalystTier(stack);
        if (catalystTier > 0) {
            list.add(I18n.format("gui.silentgear.material_grader.catalystTier", String.valueOf(catalystTier)));
        }
        return list;
    }*/

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        Component text = ModBlocks.MATERIAL_GRADER.asBlock().getName();
        font.draw(matrixStack, text.getString(), 28, 6, 0x404040);

        MaterialGrade lastAttempt = this.menu.getLastGradeAttempt();
        if (lastAttempt != MaterialGrade.NONE) {
            font.drawShadow(matrixStack, lastAttempt.getDisplayName().getVisualOrderText(), 50, 55, 0xFFFFFF);
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bind(TEXTURE);

        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);

        // Progress arrow
        blit(matrixStack, posX + 49, posY + 34, 176, 14, menu.getProgressArrowScale() + 1, 16);
    }
}

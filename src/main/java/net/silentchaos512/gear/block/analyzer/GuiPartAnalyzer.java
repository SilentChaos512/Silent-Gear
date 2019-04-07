/*
 * Silent Gear -- GuiPartAnalyzer
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.block.analyzer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

import java.util.List;

public class GuiPartAnalyzer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/analyzer.png");

    private final TilePartAnalyzer tileInventory;

    public GuiPartAnalyzer(InventoryPlayer playerInventory, TilePartAnalyzer tileInventory) {
        super(new ContainerPartAnalyzer(playerInventory, tileInventory));
        this.tileInventory = tileInventory;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public List<String> getItemToolTip(ItemStack stack) {
        List<String> list = super.getItemToolTip(stack);
        int catalystTier = TilePartAnalyzer.getCatalystTier(stack);
        if (catalystTier > 0) {
            list.add(I18n.format("gui.silentgear.part_analyzer.catalystTier", String.valueOf(catalystTier)));
        }
        return list;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ITextComponent text = ModBlocks.PART_ANALYZER.asBlock().getNameTextComponent();
        fontRenderer.drawString(text.getFormattedText(), 28, 6, 0x404040);
    }

    private int getAnalyzeProgress(int scale) {
        int progress = tileInventory.progress;
        int time = TilePartAnalyzer.BASE_ANALYZE_TIME;
        return progress > 0 && progress < time ? progress * scale / time : 0;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);

        // Progress arrow
        drawTexturedModalRect(posX + 49, posY + 34, 176, 14, getAnalyzeProgress(24) + 1, 16);
    }
}

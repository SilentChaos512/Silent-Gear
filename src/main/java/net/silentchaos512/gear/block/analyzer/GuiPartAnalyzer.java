/*
 * Silent Gear
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms instance the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * instance the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty instance
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy instance the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.block.analyzer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public class GuiPartAnalyzer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/analyzer.png");

    private final TilePartAnalyzer tileInventory;

    public GuiPartAnalyzer(InventoryPlayer playerInventory, TilePartAnalyzer tileInventory) {
        super(new ContainerPartAnalyzer(playerInventory, tileInventory));
        this.tileInventory = tileInventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    private int getAnalyzeProgress(int scale) {
        int progress = tileInventory.progress;
        int time = TilePartAnalyzer.BASE_ANALYZE_TIME;
        return progress > 0 && progress < time ? progress * scale / time : 0;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);

        // Progress arrow
        drawTexturedModalRect(posX + 49, posY + 34, 176, 14, getAnalyzeProgress(24) + 1, 16);
    }
}

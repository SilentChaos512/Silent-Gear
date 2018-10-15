/*
 * Silent Gear -- GuiSalvager
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

package net.silentchaos512.gear.block.salvager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

public class GuiSalvager extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/salvager.png");
    private final TileSalvager tileInventory;

    public GuiSalvager(InventoryPlayer playerInventory, TileSalvager tileInventory) {
        super(new ContainerSalvager(playerInventory, tileInventory));
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
        this.fontRenderer.drawString(SilentGear.i18n.translatedName(ModBlocks.salvager), 28, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);

        // Progress arrow
        drawTexturedModalRect(posX + 32, posY + 34, 176, 14, getProgressAmount(24) + 1, 16);
    }

    private int getProgressAmount(int scale) {
        int progress = tileInventory.progress;
        int time = TileSalvager.BASE_WORK_TIME;
        return progress > 0 && progress < time ? progress * scale / time : 0;
    }
}

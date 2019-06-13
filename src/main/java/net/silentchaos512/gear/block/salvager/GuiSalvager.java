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

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

public class GuiSalvager extends ContainerScreen<ContainerSalvager> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/salvager.png");
    private final TileSalvager tileInventory;

    public GuiSalvager(PlayerInventory playerInventory, TileSalvager tileInventory) {
        super(new ContainerSalvager(playerInventory, tileInventory));
        this.tileInventory = tileInventory;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(ModBlocks.SALVAGER.asBlock().getNameTextComponent().getFormattedText(), 28, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (minecraft == null) return;

        GlStateManager.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        blit(posX, posY, 0, 0, this.xSize, this.ySize);

        // Progress arrow
        blit(posX + 32, posY + 34, 176, 14, getProgressAmount(24) + 1, 16);
    }

    private int getProgressAmount(int scale) {
        int progress = tileInventory.progress;
        int time = TileSalvager.BASE_WORK_TIME;
        return progress > 0 && progress < time ? progress * scale / time : 0;
    }
}

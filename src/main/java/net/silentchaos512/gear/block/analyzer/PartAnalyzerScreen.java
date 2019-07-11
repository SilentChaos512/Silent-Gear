/*
 * Silent Gear -- PartAnalyzerScreen
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

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

import java.util.List;

public class PartAnalyzerScreen extends ContainerScreen<PartAnalyzerContainer> {
    private static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/analyzer.png");

    public PartAnalyzerScreen(PartAnalyzerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public List<String> getTooltipFromItem(ItemStack stack) {
        List<String> list = super.getTooltipFromItem(stack);
        // Add catalyst tier to tooltip, only in part analyzer
        int catalystTier = PartAnalyzerTileEntity.getCatalystTier(stack);
        if (catalystTier > 0) {
            list.add(I18n.format("gui.silentgear.part_analyzer.catalystTier", String.valueOf(catalystTier)));
        }
        return list;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ITextComponent text = ModBlocks.PART_ANALYZER.asBlock().getNameTextComponent();
        font.drawString(text.getFormattedText(), 28, 6, 0x404040);
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
        blit(posX + 49, posY + 34, 176, 14, container.getProgressArrowScale() + 1, 16);
    }
}

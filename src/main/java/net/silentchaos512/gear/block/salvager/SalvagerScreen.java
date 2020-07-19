/*
 * Silent Gear -- SalvagerScreen
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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

public class SalvagerScreen extends ContainerScreen<SalvagerContainer> {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/salvager.png");

    public SalvagerScreen(SalvagerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrix, mouseX, mouseY);
    }

    // drawGuiContainerForegroundLayer
    @Override
    protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY) {
        IFormattableTextComponent text = ModBlocks.SALVAGER.asBlock().getTranslatedName();
        this.font.drawString(matrix, text.getString(), 28, 6, 0x404040);
    }

    // drawGuiContainerBackgroundLayer
    @Override
    protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (minecraft == null) return;

        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        blit(matrix, posX, posY, 0, 0, this.xSize, this.ySize);

        // Progress arrow
        blit(matrix, posX + 32, posY + 34, 176, 14, container.getProgressArrowScale() + 1, 16);
    }
}

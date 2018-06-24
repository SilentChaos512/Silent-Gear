package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.client.util.TooltipFlagTC;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.util.StackHelper;

import java.util.List;

public class GuiCraftingStation extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/crafting_station.png");

    private final TileCraftingStation tile;
    private final ContainerCraftingStation container;

    public GuiCraftingStation(TileCraftingStation tile, ContainerCraftingStation inventorySlotsIn) {
        super(inventorySlotsIn);
        this.tile = tile;
        this.container = inventorySlotsIn;

        this.xSize = 256;
        // this.ySize = 256;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private TooltipFlagTC getTooltipFlag(ItemStack stack) {
        boolean ctrl = stack.getItem() instanceof ICoreItem || stack.getItem() instanceof ToolHead;
        boolean alt = KeyTrackerSL.isAltDown();
        boolean shift = KeyTrackerSL.isShiftDown();
        return new TooltipFlagTC(ctrl, alt, shift, false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Draw adjacent inventories
//        NonNullList<Pair<ItemStack, IInventory>> list = this.tile.getAdjacentInventories();
//        int itemX = 5;
//        for (Pair<ItemStack, IInventory> pair : list) {
//            AssetUtil.renderStackToGui(pair.getLeft(), itemX, -18, 1f);
//            itemX += 20;
//        }

        // Draw crafting result tooltip
        ItemStack craftResult = this.container.craftResult.getStackInSlot(0);
        if (StackHelper.isEmpty(craftResult))
            return;

        FontRenderer font = this.mc.fontRenderer;
        List<String> tooltip = craftResult.getTooltip(this.mc.player, getTooltipFlag(craftResult));

        int maxWidth = 0;
        for (String line : tooltip) {
            maxWidth = Math.max(maxWidth, font.getStringWidth(line));
        }
        // SilentGear.log.debug(maxWidth);

        float scale = 75f / maxWidth;
        int xPos = (int) ((this.xSize - 82) / scale);
        int yPos = (int) ((this.ySize - 160) / scale);
        // SilentGear.log.debug(xPos, yPos);

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        int step = (int) (scale * 10);
        for (String line : tooltip) {
            font.drawStringWithShadow(line, xPos, yPos, 0xFFFFFF);
            yPos += Math.round(step / scale);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Main window
        GlStateManager.color(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        // Internal/external inventory slots
        int rowCount = this.tile.getSizeInventory() / 3;
        int totalHeight = 44 + 18 * (rowCount - 2);
        int x = xPos - 61;
        int y = yPos + (this.ySize - totalHeight) / 2;
        for (int i = 0; i < rowCount; ++i) {
            int height = i == 0 || i == rowCount - 1 ? 22 : 18;
            int ty = 194 + (i == 0 ? 0 : i == rowCount - 1 ? 40 : 22);
            this.drawTexturedModalRect(x, y, 0, ty, 62, height);
            y += height;
        }

        // Tooltip background
        x = xPos + 171;
        y = yPos + 4;
        this.drawRect(x, y, x + 80, y + 158, 0xCF000000);
    }
}

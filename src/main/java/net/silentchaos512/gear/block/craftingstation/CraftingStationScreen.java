package net.silentchaos512.gear.block.craftingstation;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.gui.GuiItemParts;
import net.silentchaos512.gear.client.gui.TexturedButton;
import net.silentchaos512.gear.client.util.TooltipFlagTC;
import net.silentchaos512.lib.util.TextRenderUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CraftingStationScreen extends ContainerScreen<CraftingStationContainer> {
    private static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/crafting_station.png");

    private TexturedButton buttonShowAllParts;

    public CraftingStationScreen(CraftingStationContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);

        this.xSize = 256;
        // this.ySize = 256;
    }

    @Override
    public void init() {
        super.init();

        // Parts GUI button
        buttonShowAllParts = new TexturedButton(TEXTURE, guiLeft + 149, guiTop + 5, 236, 166, 20, 18,
                ImmutableList.of(
                        new TranslationTextComponent("gui.silentgear.crafting_station.parts_button.hover1")
                                .getFormattedText(),
                        new TranslationTextComponent("gui.silentgear.crafting_station.parts_button.hover2")
                                .applyTextStyle(TextFormatting.GRAY)
                                .getFormattedText()
                ),
                b -> {
                    if (minecraft != null) {
                        minecraft.displayGuiScreen(new GuiItemParts(new TranslationTextComponent("gui.silentgear.parts")));
                    }
                });
        this.addButton(buttonShowAllParts);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        if (buttonShowAllParts.isMouseOver(mouseX, mouseY)) { // TODO: right params?
            buttonShowAllParts.drawHover(mouseX, mouseY);
        }
    }

    private static ITooltipFlag getTooltipFlag(ItemStack stack) {
        boolean ctrl = stack.getItem() instanceof ICoreItem || KeyTracker.isControlDown();
        boolean alt = KeyTracker.isAltDown();
        boolean shift = KeyTracker.isShiftDown();
        return new TooltipFlagTC(ctrl, alt, shift, false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (minecraft == null) return;

        // Draw adjacent inventories
//        NonNullList<Pair<ItemStack, IInventory>> list = this.tile.getAdjacentInventories();
//        int itemX = 5;
//        for (Pair<ItemStack, IInventory> pair : list) {
//            AssetUtil.renderStackToGui(pair.getLeft(), itemX, -18, 1f);
//            itemX += 20;
//        }

        this.font.drawString(I18n.format("gui.silentgear.crafting_station.crafting"), 8, 6, 0x404040);
        this.font.drawString(I18n.format("gui.silentgear.crafting_station.storage"), -55, 19, 0x404040);
        this.font.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);

        // Version number (remove in full release?)
        renderVersionString();

        ItemStack craftResult = this.container.craftResult.getStackInSlot(0);
        drawSlimeFace(craftResult);

        // Debug
//        if (SilentGear.instance.isDevBuild()) {
//            Slot slot = getSlotUnderMouse();
//            if (slot != null) {
//                fontRenderer.drawString("slot=" + slot.getSlotIndex(), 130, 6, Color.VALUE_WHITE);
//            }
//        }

        if (!craftResult.isEmpty()) {
            // Draw crafting result tooltip
            List<String> tooltip = craftResult.getTooltip(this.minecraft.player, getTooltipFlag(craftResult))
                    .stream()
                    .map(ITextComponent::getFormattedText)
                    .map(s -> s.length() > 30 ? s.substring(0, 30) + "..." : s)
                    .collect(Collectors.toList());

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
            GlStateManager.scalef(scale, scale, 1);

            int step = (int) (scale * 10);
            for (String line : tooltip) {
                font.drawStringWithShadow(line, xPos, yPos, 0xFFFFFF);
                yPos += Math.round(step / scale);
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (minecraft == null) return;

        // Main window
        GlStateManager.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int xPos = (this.width - this.xSize) / 2;
        int yPos = (this.height - this.ySize) / 2;
        this.blit(xPos, yPos, 0, 0, this.xSize, this.ySize);

        // Internal/external inventory slots
        final int rowCount = CraftingStationTileEntity.SIDE_INVENTORY_SIZE / 3;
        final int rowWidth = 62;
        final int totalHeight = 44 + 18 * (rowCount - 2);
        int x = xPos - 61;
        int y = yPos + (this.ySize - totalHeight) / 2;
        for (int i = 0; i < rowCount; ++i) {
            int height = i == 0 || i == rowCount - 1 ? 22 : 18;
            int ty = 194 + (i == 0 ? 0 : i == rowCount - 1 ? 40 : 22);
            this.blit(x, y, 0, ty, rowWidth, height);
            y += height;
        }

        this.blit(x, y - totalHeight - 12, 0, 177, rowWidth, 15);

        // Tooltip background
        x = xPos + 171;
        y = yPos + 4;
        fill(x, y, x + 80, y + 158, 0xCF000000);
    }

    private void renderVersionString() {
        String versionNumString = "Version: " + SilentGear.getLongVersion() + (SilentGear.isDevBuild() ? " (dev)" : "");
        int versionNumStringWidth = font.getStringWidth(versionNumString);
        float versionNumScale = 0.65f;
        TextRenderUtils.renderScaled(font, versionNumString,
                (int) (xSize - versionNumStringWidth * versionNumScale) - 1,
                ySize + 1,
                versionNumScale,
                0xCCCCCC,
                false);
    }

    private void drawSlimeFace(ItemStack craftResult) {
        if (minecraft == null) return;

        TextureManager textureManager = minecraft.getTextureManager();
        textureManager.bindTexture(TEXTURE);
        GlStateManager.color4f(1, 1, 1, 1);
        final int textureY = 245;
        int textureX;
        if (craftResult.isEmpty()) {
            if (this.container.craftMatrix.isEmpty())
                textureX = 223; // :|
            else
                textureX = 234; // :\
        } else {
            ResourceLocation name = craftResult.getItem().getRegistryName();
            if (name == null || "tconstruct".equals(name.getNamespace()))
                textureX = 245; // :(
            else
                textureX = 212; // :)
        }

        blit(this.xSize - 18, this.ySize - 17, textureX, textureY, 11, 11);
        textureManager.bindTexture(GUI_ICONS_LOCATION);
    }
}

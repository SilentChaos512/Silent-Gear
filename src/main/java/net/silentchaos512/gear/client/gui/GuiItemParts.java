/*
 * Silent Gear -- GuiItemParts
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

package net.silentchaos512.gear.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.client.gui.button.GuiDropDownElement;
import net.silentchaos512.lib.client.gui.button.GuiDropDownList;
import net.silentchaos512.lib.util.TextRenderUtils;
import net.silentchaos512.utils.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Display part name with item tooltip, or maybe replace item name?
// TODO: Add some text explaining interface?
public class GuiItemParts extends Screen {
    private static final int BUTTON_SPACING = PartButton.SIZE + 4;
    private static final int BUTTON_ROW_LENGTH = 12;
    private static final int BUTTON_INITIAL_OFFSET = 5;
    private static final ResourceLocation TEX_WHITE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/white.png");

    private List<IGearPart> partList = new ArrayList<>();
    private List<PartButton> partButtons = new ArrayList<>();
    private IGearPart selectedPart = null;
    private List<Tuple<String, String>> selectedPartInfo = null;

    public GuiItemParts(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Override
    public void init() {
        if (minecraft == null) return;

        GuiDropDownList dropDownList = new GuiDropDownList(minecraft.mainWindow.getWidth() - 120, 5, "Sort Order", GuiDropDownList.ExpandDirection.DOWN);
        dropDownList.addElement(new GuiDropDownElement("Name", b -> sortParts(false, Comparator.comparing(part -> part.getDisplayName(null, ItemStack.EMPTY).getFormattedText()))), buttons);
        dropDownList.addElement(new GuiDropDownElement("Type", b -> sortParts(false, Comparator.comparing(part -> part.getType().getName()))), buttons);
        ItemStat.ALL_STATS.values().stream()
                .filter(stat -> !stat.isHidden())
                .forEachOrdered(stat -> {
                    String statName = stat.getDisplayName().getFormattedText();
                    dropDownList.addElement(new GuiDropDownElement(statName, b -> {
                        sortParts(true, Comparator.comparingDouble(part -> part.computeStatValue(stat)));
                    }), buttons);
                });
        buttons.add(dropDownList);

        // Build part button list
        int i = 0;
        for (IGearPart part : PartManager.getValues()) {
            if (part.isCraftingAllowed(null)) {
                partList.add(part);
                final int x = i % BUTTON_ROW_LENGTH;
                final int y = i / BUTTON_ROW_LENGTH;
                PartButton button = new PartButton(part, x * BUTTON_SPACING + BUTTON_INITIAL_OFFSET, y * BUTTON_SPACING + BUTTON_INITIAL_OFFSET, b -> {
                    selectedPart = part;
                    selectedPartInfo = getPartInfo(selectedPart);
                });
                partButtons.add(button);
                this.addButton(button);
                ++i;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void layoutPartButtons() {
        int i = 0;
        for (PartButton button : partButtons) {
            button.x = (i % BUTTON_ROW_LENGTH) * BUTTON_SPACING + BUTTON_INITIAL_OFFSET;
            button.y = (i / BUTTON_ROW_LENGTH) * BUTTON_SPACING + BUTTON_INITIAL_OFFSET;
            ++i;
        }
    }

    private void sortParts(boolean reversed, Comparator<IGearPart> comparator) {
        partList.sort(comparator);

        // Also resort the buttons... maybe not the best solution?
        List<PartButton> sortedList = new ArrayList<>();
        for (IGearPart part : partList) {
            for (PartButton button : partButtons) {
                if (button.part == part) {
                    sortedList.add(button);
                    break;
                }
            }
        }
        partButtons = sortedList;

        if (reversed) {
            partList = Lists.reverse(partList);
            partButtons = Lists.reverse(partButtons);
        }
        layoutPartButtons();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        drawSelectedPartInfo();
        super.render(mouseX, mouseY, partialTicks);

        if (minecraft == null) return;

        for (Widget button : buttons) {
            if (button instanceof PartButton) {
                ((PartButton) button).drawHover(minecraft, mouseX, mouseY);
            }
        }
    }

    private void drawSelectedPartInfo() {
        if (minecraft == null) return;

        if (selectedPart != null && !selectedPartInfo.isEmpty()) {
            ItemStack stack = new ItemStack(selectedPart.getMaterials().getItem());
//            AssetUtil.renderStackToGui(stack, res.getScaledWidth() - 194, 30, 2.5f);
            minecraft.getItemRenderer().renderItemIntoGUI(stack, minecraft.mainWindow.getScaledWidth() - 194, 30);

            final int maxWidth = 140;
            final int x = minecraft.mainWindow.getScaledWidth() - (maxWidth + 10);
            int y = 35;

            String translatedName = selectedPart.getDisplayName(PartData.of(selectedPart), ItemStack.EMPTY).getFormattedText();
            TextRenderUtils.renderScaled(minecraft.fontRenderer, translatedName, x, y, 1, Color.VALUE_WHITE, false);
            String regName = TextFormatting.GRAY + selectedPart.getId().toString();
            TextRenderUtils.renderScaled(minecraft.fontRenderer, regName, x, y + 10, 0.5f, Color.VALUE_WHITE, false);
            String typeName = selectedPart.getType().getDisplayName(selectedPart.getTier()).getFormattedText();
            TextRenderUtils.renderScaled(minecraft.fontRenderer, TextFormatting.GREEN + typeName, x, y + 16, 0.8f, Color.VALUE_WHITE, false);
            y += 30;

            for (Tuple<String, String> pair : selectedPartInfo) {
                font.drawString(pair.getA(), x, y, Color.VALUE_WHITE);
                int width2 = font.getStringWidth(pair.getB());
                font.drawString(pair.getB(), x + maxWidth - width2, y, Color.VALUE_WHITE);
                y += 10;
                // TODO: We need actual stat bars or something. Need to save actual stats, record max value somewhere.
            }
        }
    }

    private static List<Tuple<String, String>> getPartInfo(IGearPart part) {
        List<Tuple<String, String>> list = new ArrayList<>();

        PartData partData = PartData.of(part);
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, partData);

            if (!modifiers.isEmpty()) {
                StatInstance inst = stat.computeForDisplay(0, MaterialGrade.NONE, modifiers);
                if (inst.shouldList(part, stat, true)) {
                    // Just copied from TooltipHandler for now... Should probably have a Lib utility for rounding?
                    boolean isZero = inst.getValue() == 0;
                    TextFormatting nameColor = isZero ? TextFormatting.DARK_GRAY : stat.displayColor;
                    TextFormatting statColor = isZero ? TextFormatting.DARK_GRAY : TextFormatting.WHITE;
                    String nameStr = nameColor + I18n.format("stat." + stat.getName().getNamespace() + "." + stat.getName().getPath());
                    int decimalPlaces = stat.displayAsInt && inst.getOp() != StatInstance.Operation.MUL1 && inst.getOp() != StatInstance.Operation.MUL2 ? 0 : 2;

                    String statStr = statColor + inst.formattedString(decimalPlaces, false).replaceFirst("\\.0+$", "");
                    if (statStr.contains("."))
                        statStr = statStr.replaceFirst("0+$", "");
                    if (modifiers.size() > 1)
                        statStr += "*";
                    if (stat == ItemStats.ARMOR_DURABILITY)
                        statStr += "x";

                    list.add(new Tuple<>(nameStr, statStr));
                }
            }
        }

        return list;
    }

    public static class PartButton extends Button {
        private static final int SIZE = 16;

        final IGearPart part;

        PartButton(IGearPart part, int x, int y, IPressable action) {
            this(part, x, y, SIZE, SIZE, action);
        }

        PartButton(IGearPart part, int x, int y, int widthIn, int heightIn, IPressable action) {
            super(x, y, widthIn, heightIn, part.getId().toString(), action);
            this.part = part;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

                // Render item TODO: would it be possible to get all tagged items?
                ItemStack stack = new ItemStack(part.getMaterials().getItem());
                if (stack.isEmpty()) {
                    stack = new ItemStack(Blocks.BARRIER);
                }
                GlStateManager.enableRescaleNormal();
                RenderHelper.enableGUIStandardItemLighting();
                Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, this.x, this.y);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
            }
        }

        void drawHover(Minecraft mc, int mouseX, int mouseY) {
            if (this.isMouseOver(mouseX, mouseY)) {
                ItemStack craftingStack = new ItemStack(part.getMaterials().getItem());
                List<String> tooltip = craftingStack.getTooltip(mc.player, () -> false)
                        .stream()
                        .map(ITextComponent::getFormattedText)
                        .collect(Collectors.toList());
                GuiUtils.preItemToolTip(craftingStack);
                tooltip.add(0, part.getDisplayName(null, ItemStack.EMPTY).applyTextStyle(TextFormatting.UNDERLINE).getFormattedText());
                GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, mc.mainWindow.getWidth(), mc.mainWindow.getHeight(), -1, mc.fontRenderer);
                GuiUtils.postItemToolTip();
            }
        }
    }
}

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.client.gui.button.GuiDropDownElement;
import net.silentchaos512.lib.client.gui.button.GuiDropDownList;
import net.silentchaos512.utils.Color;
import net.silentchaos512.lib.util.TextRenderUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Display part name with item tooltip, or maybe replace item name?
// TODO: Add some text explaining interface?
public class GuiItemParts extends GuiScreen {
    private static final int BUTTON_SPACING = PartButton.SIZE + 4;
    private static final int BUTTON_ROW_LENGTH = 12;
    private static final int BUTTON_INITIAL_OFFSET = 5;
    private static final ResourceLocation TEX_WHITE = new ResourceLocation(SilentGear.MOD_ID, "textures/gui/white.png");

    private int lastButtonId = 6900;
    private List<IGearPart> partList = new ArrayList<>();
    private List<PartButton> partButtons = new ArrayList<>();
    private IGearPart selectedPart = null;
    private List<StringPair> selectedPartInfo = null;

    @Override
    public void initGui() {
        super.initGui();

        GuiDropDownList dropDownList = new GuiDropDownList(lastButtonId++, mc.mainWindow.getWidth() - 120, 5, "Sort Order", GuiDropDownList.ExpandDirection.DOWN);
        dropDownList.addElement(new GuiDropDownElement(lastButtonId++, "Name",
                b -> sortParts(false, Comparator.comparing(part -> part.getDisplayName(null, ItemStack.EMPTY).getFormattedText()))) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                selectedPart = null;
                if (selectedPartInfo != null) selectedPartInfo.clear();
            }
        }, buttons);
        dropDownList.addElement(new GuiDropDownElement(lastButtonId++, "Type",
                b -> sortParts(false, Comparator.comparing(part -> part.getType().getName()))) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                selectedPart = null;
                if (selectedPartInfo != null) selectedPartInfo.clear();
            }
        }, buttons);
        ItemStat.ALL_STATS.values().stream()
                .filter(stat -> !stat.isHidden())
                .forEachOrdered(stat -> dropDownList.addElement(new GuiDropDownElement(lastButtonId++, stat.translatedName(),
                        b -> sortParts(true, Comparator.comparingDouble(part -> part.computeStatValue(stat)))), buttons));
        buttons.add(dropDownList);

        // Build part button list
        int i = 0;
        for (IGearPart part : PartManager.getValues()) {
            if (true /*!part.isBlacklisted()*/) {
                partList.add(part);
                final int x = i % BUTTON_ROW_LENGTH;
                final int y = i / BUTTON_ROW_LENGTH;
                PartButton button = new PartButton(part, lastButtonId++, x * BUTTON_SPACING + BUTTON_INITIAL_OFFSET, y * BUTTON_SPACING + BUTTON_INITIAL_OFFSET) {
                    @Override
                    public void onClick(double mouseX, double mouseY) {
                        selectedPart = this.part;
                        selectedPartInfo = getPartInfo(selectedPart);
                    }
                };
                partButtons.add(button);
                buttons.add(button);
                ++i;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
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
        this.drawDefaultBackground();

        drawSelectedPartInfo();

        super.render(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttons) {
            if (button instanceof PartButton) {
                ((PartButton) button).drawHover(mc, mouseX, mouseY);
            }
        }
    }

    private void drawSelectedPartInfo() {
        if (selectedPart != null && !selectedPartInfo.isEmpty()) {
            ItemStack stack = new ItemStack(selectedPart.getMaterials().getItem());
//            AssetUtil.renderStackToGui(stack, res.getScaledWidth() - 194, 30, 2.5f);
            mc.getItemRenderer().renderItemIntoGUI(stack, mc.mainWindow.getWidth() - 194, 30);

            final int maxWidth = 140;
            final int x = mc.mainWindow.getWidth() - (maxWidth + 10);
            int y = 35;

            String translatedName = selectedPart.getDisplayName(PartData.of(selectedPart), ItemStack.EMPTY).getFormattedText();
            TextRenderUtils.renderScaled(mc.fontRenderer, translatedName, x, y, 1, Color.VALUE_WHITE, false);
            String regName = TextFormatting.GRAY + selectedPart.getId().toString();
            TextRenderUtils.renderScaled(mc.fontRenderer, regName, x, y + 10, 0.5f, Color.VALUE_WHITE, false);
            String typeName = I18n.format("part.silentgear.type." + selectedPart.getType().getName(), selectedPart.getTier());
            TextRenderUtils.renderScaled(mc.fontRenderer, TextFormatting.GREEN + typeName, x, y + 16, 0.8f, Color.VALUE_WHITE, false);
            y += 30;

            for (StringPair pair : selectedPartInfo) {
                fontRenderer.drawString(pair.first, x, y, Color.VALUE_WHITE);
                int width2 = fontRenderer.getStringWidth(pair.second);
                fontRenderer.drawString(pair.second, x + maxWidth - width2, y, Color.VALUE_WHITE);
                y += 10;
                // TODO: We need actual stat bars or something. Need to save actual stats, record max value somewhere.
            }
        }
    }

    private List<StringPair> getPartInfo(IGearPart part) {
        List<StringPair> list = new ArrayList<>();

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
                    if (stat == CommonItemStats.ARMOR_DURABILITY)
                        statStr += "x";

                    list.add(StringPair.of(nameStr, statStr));
                }
            }
        }

        return list;
    }

    public static class PartButton extends GuiButton {
        private static final int SIZE = 16;

        final IGearPart part;

        PartButton(IGearPart part, int buttonId, int x, int y) {
            this(part, buttonId, x, y, SIZE, SIZE);
        }

        PartButton(IGearPart part, int buttonId, int x, int y, int widthIn, int heightIn) {
            super(buttonId, x, y, widthIn, heightIn, part.getId().toString());
            this.part = part;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

                // Render item TODO: would it be possible to get all tagged items?
                ItemStack stack = new ItemStack(part.getMaterials().getItem());
                Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, this.x, this.y);
            }
        }

        void drawHover(Minecraft mc, int mouseX, int mouseY) {
            if (this.isMouseOver()) {
                ItemStack craftingStack = new ItemStack(part.getMaterials().getItem());
                List<String> tooltip = craftingStack.getTooltip(mc.player, () -> false)
                        .stream()
                        .map(ITextComponent::getFormattedText)
                        .collect(Collectors.toList());
                GuiUtils.preItemToolTip(craftingStack);
                GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, mc.mainWindow.getWidth(), mc.mainWindow.getHeight(), -1, mc.fontRenderer);
                GuiUtils.postItemToolTip();
            }
        }
    }

    private static final class StringPair {
        private final String first;
        private final String second;

        private StringPair(String first, String second) {
            this.first = first;
            this.second = second;
        }

        private static StringPair of() {
            return of("", "");
        }

        private static StringPair of(String first) {
            return of(first, "");
        }

        private static StringPair of(String first, String second) {
            return new StringPair(first, second);
        }
    }
}

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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.lib.client.gui.button.GuiDropDownElement;
import net.silentchaos512.lib.client.gui.button.GuiDropDownList;
import net.silentchaos512.lib.util.AssetUtil;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

// TODO: Display part name with item tooltip, or maybe replace item name?
// TODO: Add some text explaining interface?
public class GuiItemParts extends GuiScreen {
    private static final int BUTTON_SPACING = PartButton.SIZE + 4;
    private static final int BUTTON_ROW_LENGTH = 12;
    private static final int BUTTON_INITIAL_OFFSET = 5;

    private int lastButtonId = 6900;
    private List<ItemPart> partList = new ArrayList<>();
    private List<PartButton> partButtons = new ArrayList<>();
    private ItemPart selectedPart = null;
    private List<StringPair> selectedPartInfo = null;

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution res = new ScaledResolution(mc);

        GuiDropDownList dropDownList = new GuiDropDownList(lastButtonId++, res.getScaledWidth() - 120, 5, "Sort Order", GuiDropDownList.ExpandDirection.DOWN);
        dropDownList.addElement(new GuiDropDownElement(lastButtonId++, "Name",
                b -> sortParts(false, Comparator.comparing(part -> part.getTranslatedName(null, ItemStack.EMPTY)))), buttonList);
        dropDownList.addElement(new GuiDropDownElement(lastButtonId++, "Type",
                b -> sortParts(false, Comparator.comparing(part -> part.getType().getName()))), buttonList);
        ItemStat.ALL_STATS.values().stream()
                .filter(stat -> !stat.isHidden())
                .forEachOrdered(stat -> dropDownList.addElement(new GuiDropDownElement(lastButtonId++, stat.translatedName(),
                        b -> sortParts(true, Comparator.comparingDouble(part -> part.computeStatValue(stat)))), buttonList));
        buttonList.add(dropDownList);

        // Build part button list
        int i = 0;
        for (ItemPart part : PartRegistry.getValues()) {
            if (!part.isBlacklisted()) {
                partList.add(part);
                final int x = i % BUTTON_ROW_LENGTH;
                final int y = i / BUTTON_ROW_LENGTH;
                PartButton button = new PartButton(part, lastButtonId++,
                        x * BUTTON_SPACING + BUTTON_INITIAL_OFFSET, y * BUTTON_SPACING + BUTTON_INITIAL_OFFSET);
                partButtons.add(button);
                buttonList.add(button);
                ++i;
            }
        }
    }

    private void layoutPartButtons() {
        int i = 0;
        for (PartButton button : partButtons) {
            button.x = (i % BUTTON_ROW_LENGTH) * BUTTON_SPACING + BUTTON_INITIAL_OFFSET;
            button.y = (i / BUTTON_ROW_LENGTH) * BUTTON_SPACING + BUTTON_INITIAL_OFFSET;
            ++i;
        }
    }

    private void sortParts(boolean reversed, Comparator<ItemPart> comparator) {
        partList.sort(comparator);

        // Also resort the buttons... maybe not the best solution?
        List<PartButton> sortedList = new ArrayList<>();
        for (ItemPart part : partList) {
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

    @SuppressWarnings("ChainOfInstanceofChecks")
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof PartButton) {
            PartButton partButton = (PartButton) button;
            selectedPart = partButton.part;
            selectedPartInfo = getPartInfo(selectedPart);
        } else if (button instanceof GuiDropDownElement) {
            selectedPart = null;
            if (selectedPartInfo != null) selectedPartInfo.clear();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        drawSelectedPartInfo();

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttonList) {
            if (button instanceof PartButton) {
                ((PartButton) button).drawHover(mc, mouseX, mouseY);
            }
        }
    }

    private void drawSelectedPartInfo() {
        if (selectedPart != null && !selectedPartInfo.isEmpty()) {
            final int maxWidth = 140;
            final int x = new ScaledResolution(mc).getScaledWidth() - (maxWidth + 10);
            int y = 35;

            String translatedName = selectedPart.getTranslatedName(ItemPartData.instance(selectedPart), ItemStack.EMPTY);
            StringUtil.renderScaledAsciiString(mc.fontRenderer, translatedName, x, y, Color.VALUE_WHITE, false, 1);
            String regName = TextFormatting.GRAY + "(" + selectedPart.getRegistryName() + ")";
            StringUtil.renderScaledAsciiString(mc.fontRenderer, regName, x, y + 10, Color.VALUE_WHITE, false, 0.5f);
            String typeName = SilentGear.i18n.translate("part", "type." + selectedPart.getType().getName(), selectedPart.getTier());
            StringUtil.renderScaledAsciiString(mc.fontRenderer, TextFormatting.GREEN + typeName, x, y + 16, Color.VALUE_WHITE, false, 0.8f);
            y += 30;

            for (StringPair pair : selectedPartInfo) {
                fontRenderer.drawString(pair.first, x, y, Color.VALUE_WHITE);
                int width2 = fontRenderer.getStringWidth(pair.second);
                fontRenderer.drawString(pair.second, x + maxWidth - width2, y, Color.VALUE_WHITE);
                y += 10;
            }
        }
    }

    private List<StringPair> getPartInfo(ItemPart part) {
        List<StringPair> list = new ArrayList<>();

        ItemPartData partData = ItemPartData.instance(part);
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, partData);

            if (!modifiers.isEmpty()) {
                StatInstance inst = stat.computeForDisplay(0, MaterialGrade.NONE, modifiers);
                if (inst.shouldList(part, stat, true)) {
                    // Just copied from TooltipHandler for now... Should probably have a Lib utility for rounding?
                    boolean isZero = inst.getValue() == 0;
                    TextFormatting nameColor = isZero ? TextFormatting.DARK_GRAY : stat.displayColor;
                    TextFormatting statColor = isZero ? TextFormatting.DARK_GRAY : TextFormatting.WHITE;
                    String nameStr = nameColor + SilentGear.i18n.translate("stat." + stat.getName());
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

        private final ItemPart part;

        PartButton(ItemPart part, int buttonId, int x, int y) {
            this(part, buttonId, x, y, SIZE, SIZE);
        }

        PartButton(ItemPart part, int buttonId, int x, int y, int widthIn, int heightIn) {
            super(buttonId, x, y, widthIn, heightIn, part.getRegistryName().toString());
            this.part = part;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                AssetUtil.renderStackToGui(part.getCraftingStack(), this.x, this.y, 1f);
            }
        }

        void drawHover(Minecraft mc, int mouseX, int mouseY) {
            if (this.isMouseOver()) {
                ScaledResolution res = new ScaledResolution(mc);
                ItemStack craftingStack = part.getCraftingStack();
                List<String> tooltip = craftingStack.getTooltip(mc.player, () -> false);
                GuiUtils.preItemToolTip(craftingStack);
                GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, res.getScaledWidth(), res.getScaledHeight(), -1, mc.fontRenderer);
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

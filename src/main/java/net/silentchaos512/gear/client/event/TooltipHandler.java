package net.silentchaos512.gear.client.event;

import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.util.I18nHelper;

import java.util.Collection;

public class TooltipHandler {
    public static TooltipHandler INSTANCE = new TooltipHandler();

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ItemPart part = !stack.isEmpty() ? PartRegistry.get(stack) : null;

        if (part != null && !part.isBlacklisted(stack)) {
            onPartTooltip(event, stack, part);
            return;
        }

        // Nerfed gear?
        ResourceLocation name = stack.getItem().getRegistryName();
        if (name != null && Config.nerfedGear.contains(name.toString())) {
            event.getToolTip().add(1, TextFormatting.RED + SilentGear.i18n.translate("misc", "poorlyMade"));
        }
    }

    private void onPartTooltip(ItemTooltipEvent event, ItemStack stack, ItemPart part) {
        event.getToolTip().add(TextFormatting.GREEN + SilentGear.i18n.translate("part", "type." + part.getTypeName(), part.getTier()));

        MaterialGrade grade = MaterialGrade.fromStack(stack);
        if (KeyTrackerSL.isControlDown()) {
            if (part instanceof PartMain)
                getGradeLine(event, grade, SilentGear.i18n);
            event.getToolTip().add(TextFormatting.GOLD + SilentGear.i18n.translate("misc", "tooltip.stats.name")
                    + TextFormatting.RESET + TextFormatting.ITALIC + " (Silent Gear)");
            getPartStatLines(event, stack, part);
        } else {
            if (grade != MaterialGrade.NONE && part instanceof PartMain)
                getGradeLine(event, grade, SilentGear.i18n);
            event.getToolTip().add(TextFormatting.GOLD + SilentGear.i18n.translate("misc", "tooltip.ctrlForStats"));
        }
    }

    private void getGradeLine(ItemTooltipEvent event, MaterialGrade grade, I18nHelper i18n) {
        String line = i18n.translate("material", "gradeOnPart", grade.getTranslatedName());
        event.getToolTip().add(TextFormatting.AQUA + line);
    }

    private void getPartStatLines(ItemTooltipEvent event, ItemStack stack, ItemPart part) {
        ItemPartData partData = ItemPartData.instance(part, MaterialGrade.fromStack(stack), stack);
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, partData);

            if (!modifiers.isEmpty()) {
                StatInstance inst = stat.computeForDisplay(0, partData.getGrade(), modifiers);
                boolean isZero = inst.getValue() == 0;
                if (part instanceof PartMain && stat == CommonItemStats.HARVEST_LEVEL)
                    isZero = false;

                if (!isZero || event.getFlags() == TooltipFlags.ADVANCED) {
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

                    event.getToolTip().add("- " + SilentGear.i18n.translate("stat", "displayFormat", nameStr, statStr));
                }
            }
        }
    }
}

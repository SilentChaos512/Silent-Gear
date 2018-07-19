package net.silentchaos512.gear.client.event;

import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.lib.MaterialGrade;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.StackHelper;

import java.util.Collection;
import java.util.regex.Pattern;

public class TooltipHandler {

    public static TooltipHandler INSTANCE = new TooltipHandler();

    // TODO: Probably need more control for vanilla gear nerfing...
    private static Pattern VANILLA_TOOL_REGEX = Pattern.compile("^minecraft:(wooden|stone|iron|golden|diamond)_(pickaxe|shovel|axe|sword|hoe)");
    private static Pattern VANILLA_ARMOR_REGEX = Pattern.compile("^minecraft:(leather|chainmail|iron|golden|diamond)_(helmet|chestplate|leggings|boots)");

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ItemPart part = StackHelper.isValid(stack) ? PartRegistry.get(stack) : null;

        LocalizationHelper loc = SilentGear.localization;

        if (part != null && !part.isBlacklisted(stack)) {
            onPartTooltip(event, stack, part, loc);
            return;
        }

        // Nerfed vanilla gear?
        ResourceLocation name = stack.getItem().getRegistryName();
        if (name != null && (VANILLA_TOOL_REGEX.matcher(name.toString()).matches() || VANILLA_ARMOR_REGEX.matcher(name.toString()).matches())) {
            event.getToolTip().add(1, TextFormatting.RED + loc.getMiscText("poorlyMade"));
        }
    }

    private void onPartTooltip(ItemTooltipEvent event, ItemStack stack, ItemPart part, LocalizationHelper loc) {
        event.getToolTip().add(loc.getLocalizedString("part", "type." + part.getTypeName()));

        MaterialGrade grade = MaterialGrade.fromStack(stack);
        if (KeyTrackerSL.isControlDown()) {
            if (part instanceof PartMain)
                getGradeLine(event, grade, loc);
            event.getToolTip().add(TextFormatting.GOLD + loc.getMiscText("tooltip.stats.name"));
            getPartStatLines(event, stack, part, loc);
        } else {
            if (grade != MaterialGrade.NONE && part instanceof PartMain)
                getGradeLine(event, grade, loc);
            event.getToolTip().add(TextFormatting.GOLD + loc.getMiscText("tooltip.ctrlForStats"));
        }
    }

    private void getGradeLine(ItemTooltipEvent event, MaterialGrade grade, LocalizationHelper loc) {
        String line = loc.getLocalizedString("material", "gradeOnPart", grade.getTranslatedName());
        event.getToolTip().add(TextFormatting.AQUA + line);
    }

    private void getPartStatLines(ItemTooltipEvent event, ItemStack stack, ItemPart part, LocalizationHelper loc) {
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, stack);

            if (!modifiers.isEmpty()) {
                StatInstance inst = stat.computeForDisplay(0, modifiers);
                boolean isZero = inst.getValue() == 0;
                if (part instanceof PartMain && stat == CommonItemStats.HARVEST_LEVEL)
                    isZero = false;

                if (!isZero || event.getFlags() == TooltipFlags.ADVANCED) {
                    TextFormatting nameColor = isZero ? TextFormatting.DARK_GRAY : stat.displayColor;
                    TextFormatting statColor = isZero ? TextFormatting.DARK_GRAY : TextFormatting.WHITE;
                    String nameStr = nameColor + loc.getLocalizedString("stat", stat.getUnlocalizedName() + ".name");
                    int decimalPlaces = stat.displayAsInt && inst.getOp() != StatInstance.Operation.MUL1 && inst.getOp() != StatInstance.Operation.MUL2 ? 0 : 2;

                    String statStr = statColor + inst.formattedString(decimalPlaces, false).replaceFirst("\\.0+$", "");
                    if (statStr.contains("."))
                        statStr = statStr.replaceFirst("0+$", "");
                    if (modifiers.size() > 1)
                        statStr += "*";

                    event.getToolTip().add("- " + loc.getLocalizedString("stat", "displayFormat", nameStr, statStr));
                }
            }
        }
    }
}

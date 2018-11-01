package net.silentchaos512.gear.client.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.lib.client.key.KeyTrackerSL;

import java.util.Collection;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, value = Side.CLIENT)
public final class TooltipHandler {
    private TooltipHandler() {}

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        LoaderState state = Loader.instance().getLoaderState();
        if (state == LoaderState.INITIALIZATION || state == LoaderState.SERVER_ABOUT_TO_START || state == LoaderState.SERVER_STOPPING) {
            // Skip tooltips during block/item remapping
            // JEI tooltip caches are done in AVAILABLE, in-game is SERVER_STARTED
            return;
        }

        ItemStack stack = event.getItemStack();
        ItemPart part = !stack.isEmpty() ? PartRegistry.get(stack) : null;

        if (part != null && !part.isBlacklisted(stack)) {
            onPartTooltip(event, stack, ItemPartData.instance(part));
            return;
        }

        // Nerfed gear?
        ResourceLocation name = stack.getItem().getRegistryName();
        if (name != null && Config.nerfedGear.contains(name.toString())) {
            event.getToolTip().add(1, TextFormatting.RED + SilentGear.i18n.translate("misc", "poorlyMade"));
        }
    }

    private static void onPartTooltip(ItemTooltipEvent event, ItemStack stack, ItemPartData partData) {
        ItemPart part = partData.getPart();
        event.getToolTip().add(TextFormatting.GREEN + SilentGear.i18n.translate("part",
                "type." + part.getType().getName(), part.getTier()));

        // Traits
        Map<Trait, Integer> traits = partData.getTraits();
        for (Trait trait : traits.keySet()) {
            final int level = traits.get(trait);
            final TextFormatting nameColor = trait.getNameColor();
            event.getToolTip().add(nameColor + trait.getTranslatedName(level));
        }

        MaterialGrade grade = MaterialGrade.fromStack(stack);
        if (KeyTrackerSL.isControlDown()) {
            if (part instanceof PartMain)
                getGradeLine(event, grade);
            event.getToolTip().add(TextFormatting.GOLD + SilentGear.i18n.translate("misc", "tooltip.stats.name")
                    + TextFormatting.RESET + TextFormatting.ITALIC + " (Silent Gear)");
            getPartStatLines(event, stack, part);
        } else {
            if (grade != MaterialGrade.NONE && part instanceof PartMain)
                getGradeLine(event, grade);
            event.getToolTip().add(TextFormatting.GOLD + SilentGear.i18n.translate("misc", "tooltip.ctrlForStats"));
        }
    }

    private static void getGradeLine(ItemTooltipEvent event, MaterialGrade grade) {
        String line = SilentGear.i18n.translate("material", "gradeOnPart", grade.getTranslatedName());
        event.getToolTip().add(TextFormatting.AQUA + line);
    }

    private static void getPartStatLines(ItemTooltipEvent event, ItemStack stack, ItemPart part) {
        ItemPartData partData = ItemPartData.instance(part, MaterialGrade.fromStack(stack), stack);
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, partData);

            if (!modifiers.isEmpty()) {
                StatInstance inst = stat.computeForDisplay(0, partData.getGrade(), modifiers);
                if (inst.shouldList(part, stat, event.getFlags().isAdvanced())) {
                    boolean isZero = inst.getValue() == 0;
                    TextFormatting nameColor = isZero ? TextFormatting.DARK_GRAY : stat.displayColor;
                    TextFormatting statColor = isZero ? TextFormatting.DARK_GRAY : TextFormatting.WHITE;
                    String nameStr = nameColor + stat.translatedName();
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

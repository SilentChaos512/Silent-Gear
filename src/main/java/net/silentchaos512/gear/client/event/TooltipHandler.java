package net.silentchaos512.gear.client.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.event.ClientTicks;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class TooltipHandler {
    public static final TooltipHandler INSTANCE = new TooltipHandler();

    // Display a single trait and cycling through the list. Main problem with this is it affects
    // JEI's tooltip cache. When disabled, you can search for parts with certain traits.
    private static final boolean TRAIT_DISPLAY_CYCLE = false;

    private TooltipHandler() {}

    @SubscribeEvent(receiveCanceled = true)
    public void onTooltip(ItemTooltipEvent event) {
        /*
        LoaderState state = Loader.instance().getLoaderState();
        if (state == LoaderState.INITIALIZATION || state == LoaderState.SERVER_ABOUT_TO_START || state == LoaderState.SERVER_STOPPING) {
            // Skip tooltips during block/item remapping
            // JEI tooltip caches are done in AVAILABLE, in-game is SERVER_STARTED
            return;
        }
        */

        ItemStack stack = event.getItemStack();

        MaterialInstance material = MaterialInstance.from(stack);
        if (material != null) {
            onMaterialTooltip(event, stack, material);
//            return;
        }

        IGearPart part = !stack.isEmpty() ? PartManager.from(stack) : null;
        if (part != null /*&& !part.isBlacklisted(stack)*/) {
            onPartTooltip(event, stack, PartData.of(part, stack));
            return;
        }

        // Nerfed gear?
        if (Config.Server.isNerfedItem(stack.getItem())) {
            List<ITextComponent> toolTip = event.getToolTip();
            toolTip.add(Math.min(1, toolTip.size()), new TranslationTextComponent("misc.silentgear.poorlyMade").applyTextStyle(TextFormatting.RED));
        }
    }

    private static void onMaterialTooltip(ItemTooltipEvent event, ItemStack stack, IMaterialInstance material) {
        if (Config.Client.disableNewMaterialTooltips.get()) return;

        event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.material").applyTextStyle(TextFormatting.GOLD));
        if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(new StringTextComponent("Material ID: " + material.getMaterialId()).applyTextStyle(TextFormatting.DARK_GRAY));
        }
        if (KeyTracker.isControlDown()) {
            getGradeLine(event, material.getGrade());
/*            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.stats")
                    .applyTextStyle(TextFormatting.GOLD)
                    .appendSibling(new StringTextComponent(" (Silent Gear)")
                            .applyTextStyle(TextFormatting.RESET)
                            .applyTextStyle(TextFormatting.ITALIC)));
            getPartStatLines(event, stack, part);*/
        } else {
            if (material.getGrade() != MaterialGrade.NONE) {
                getGradeLine(event, material.getGrade());
            }
//            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.ctrlForStats").applyTextStyle(TextFormatting.GOLD));
        }
    }

    private static void onPartTooltip(ItemTooltipEvent event, ItemStack stack, PartData partData) {
        IGearPart part = partData.getPart();

        // Type, tier
        event.getToolTip().add(part.getType().getDisplayName(partData.getTier()).applyTextStyle(TextFormatting.GREEN));

        if (event.getFlags().isAdvanced() && KeyTracker.isControlDown()) {
            event.getToolTip().add(new StringTextComponent("* Part ID: " + part.getId()).applyTextStyle(TextFormatting.DARK_GRAY));
            event.getToolTip().add(new StringTextComponent("* Part data pack: " + part.getPackName()).applyTextStyle(TextFormatting.DARK_GRAY));
        }

        // Traits
        List<PartTraitInstance> traits = partData.getTraits().stream()
                .filter(inst -> inst.getTrait().showInTooltip(event.getFlags()))
                .collect(Collectors.toList());
        int numTraits = traits.size();
        int traitIndex = getTraitDisplayIndex(numTraits);
        int i = 0;
        for (PartTraitInstance inst : traits) {
            if (traitIndex < 0 || traitIndex == i) {
                inst.getTrait().addInformation(inst.getLevel(), event.getToolTip(), event.getFlags());
            }
            ++i;
        }

        if (KeyTracker.isControlDown()) {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.stats")
                    .applyTextStyle(TextFormatting.GOLD)
                    .appendSibling(new StringTextComponent(" (Silent Gear)")
                            .applyTextStyle(TextFormatting.RESET)
                            .applyTextStyle(TextFormatting.ITALIC)));
            getPartStatLines(event, stack, part);
        } else {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.ctrlForStats").applyTextStyle(TextFormatting.GOLD));
        }

        // Gear type blacklist?
        if (part instanceof AbstractGearPart) {
            List<GearType> blacklist = ((AbstractGearPart) part).getBlacklistedGearTypes();
            if (!blacklist.isEmpty()) {
                int index = (ClientTicks.ticksInGame() / 20) % blacklist.size();
                GearType gearType = blacklist.get(index);
                ITextComponent typeName = gearType.getDisplayName().applyTextStyle(TextFormatting.WHITE);
                event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.blacklist", typeName).applyTextStyle(TextFormatting.RED));
            }
        }
    }

    private static int getTraitDisplayIndex(int numTraits) {
        if (!TRAIT_DISPLAY_CYCLE || KeyTracker.isControlDown() || numTraits == 0)
            return -1;
        return ClientTicks.ticksInGame() / 20 % numTraits;
    }

    private static void getGradeLine(ItemTooltipEvent event, MaterialGrade grade) {
        event.getToolTip().add(new TranslationTextComponent("part.silentgear.gradeOnPart", grade.getDisplayName().applyTextStyle(TextFormatting.AQUA)));
    }

    private static void getPartStatLines(ItemTooltipEvent event, ItemStack stack, IGearPart part) {
        PartData partData = PartData.of(part, stack);
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, partData);

            if (!modifiers.isEmpty()) {
                StatInstance inst = stat.computeForDisplay(0, modifiers);
                if (inst.shouldList(part, stat, event.getFlags().isAdvanced())) {
                    boolean isZero = inst.getValue() == 0;
                    TextFormatting nameColor = isZero ? TextFormatting.DARK_GRAY : stat.getNameColor();
                    TextFormatting statColor = isZero ? TextFormatting.DARK_GRAY : TextFormatting.WHITE;
                    ITextComponent nameStr = stat.getDisplayName().applyTextStyle(nameColor);
                    int decimalPlaces = stat.isDisplayAsInt() && inst.getOp() != StatInstance.Operation.MUL1 && inst.getOp() != StatInstance.Operation.MUL2 ? 0 : 2;

//                    String statStr = statColor + inst.formattedString(decimalPlaces, false);
//                    if (stat == ItemStats.ARMOR_DURABILITY)
//                        statStr += "x";
//                    if (modifiers.size() > 1)
//                        statStr += "*";
                    ITextComponent statListText = StatModifierMap.formatText(modifiers, stat, decimalPlaces).applyTextStyle(statColor);

                    event.getToolTip().add(new StringTextComponent("- ")
                            .appendSibling(new TranslationTextComponent("stat.silentgear.displayFormat", nameStr, statListText.getFormattedText())));
                }
            }
        }
    }
}

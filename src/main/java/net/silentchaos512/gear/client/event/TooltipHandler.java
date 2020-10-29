package net.silentchaos512.gear.client.event;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.text.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.block.grader.GraderTileEntity;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.AbstractGearPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.utils.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TooltipHandler {
    public static final TooltipHandler INSTANCE = new TooltipHandler();

    // Display a single trait and cycling through the list. Main problem with this is it affects
    // JEI's tooltip cache. When disabled, you can search for parts with certain traits.
    private static final boolean TRAIT_DISPLAY_CYCLE = false;

    static {
        assert TextFormatting.DARK_GRAY.getColor() != null;
        assert TextFormatting.GRAY.getColor() != null;
    }
    public static final Color MC_DARK_GRAY = new Color(TextFormatting.DARK_GRAY.getColor());
    public static final Color MC_GRAY = new Color(TextFormatting.GRAY.getColor());

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

        if (isGraderCatalystWithHackyWorkaround(stack)) {
            onGraderCatalystTooltip(event);
        }

        MaterialInstance material = MaterialInstance.from(stack);
        if (material != null) {
            onMaterialTooltip(event, stack, material);
            return;
        }

        PartData part = PartData.from(stack);
        if (part != null /*&& !part.isBlacklisted(stack)*/) {
            onPartTooltip(event, stack, part);
            return;
        }

        // Nerfed gear?
        if (Config.Common.isNerfedItem(stack.getItem())) {
            List<ITextComponent> toolTip = event.getToolTip();
            toolTip.add(Math.min(1, toolTip.size()), new TranslationTextComponent("misc.silentgear.poorlyMade").mergeStyle(TextFormatting.RED));
        }
    }

    private static boolean isGraderCatalystWithHackyWorkaround(ItemStack stack) {
        // Workaround for https://github.com/SilentChaos512/Silent-Gear/issues/224 and related issues...
        // This crash only happens on the client in some cases
        try {
            return stack.getItem().isIn(ModTags.Items.GRADER_CATALYSTS);
        } catch (IllegalStateException ex) {
            TagRegistryManager.fetchTags();
        }
        return stack.getItem().isIn(ModTags.Items.GRADER_CATALYSTS);
    }

    private static void onGraderCatalystTooltip(ItemTooltipEvent event) {
        int tier = GraderTileEntity.getCatalystTier(event.getItemStack());
        event.getToolTip().add(TextUtil.withColor(TextUtil.misc("graderCatalyst", tier), Color.REBECCAPURPLE));
    }

    private static void onMaterialTooltip(ItemTooltipEvent event, ItemStack stack, MaterialInstance material) {
        boolean keyHeld = KeyTracker.isDisplayStatsDown();

        if (keyHeld) {
            event.getToolTip().add(TextUtil.withColor(TextUtil.misc("tooltip.material"), Color.GOLD));
        } else {
            event.getToolTip().add(TextUtil.withColor(TextUtil.misc("tooltip.material"), Color.GOLD)
                    .append(new StringTextComponent(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_STATS), TextFormatting.GRAY))));
        }

        if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(new StringTextComponent("Material ID: " + material.getMaterialId()).mergeStyle(TextFormatting.DARK_GRAY));
        }

        if (keyHeld) {
            getGradeLine(event, material.getGrade());

            List<PartType> partTypes = new ArrayList<>(material.getPartTypes());
            if (!partTypes.isEmpty()) {
                int index = KeyTracker.getMaterialCycleIndex(partTypes.size());
                PartType partType = partTypes.get(index);
                event.getToolTip().add(buildPartTypeHeader(partTypes, partType));
                event.getToolTip().add(TextUtil.withColor(TextUtil.misc("tier", material.getTier(partType)), Color.DEEPSKYBLUE));

                getMaterialTraitLines(event, partType, material);

                event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.stats").mergeStyle(TextFormatting.GOLD));
                getMaterialStatLines(event, partType, material);
            }
        } else {
            if (material.getGrade() != MaterialGrade.NONE) {
                getGradeLine(event, material.getGrade());
            }
        }
    }

    private static ITextComponent buildPartTypeHeader(Collection<PartType> types, PartType selectedType) {
        IFormattableTextComponent ret = new StringTextComponent("| ").mergeStyle(TextFormatting.GRAY);
        for (PartType type : types) {
            Color color = type == selectedType ? Color.AQUAMARINE : MC_DARK_GRAY;
            ITextComponent text = TextUtil.withColor(type.getDisplayName(-1), color);
            ret.append(text).appendString(" | ");
        }

        ITextComponent keyHint = TextUtil.misc("tooltip.material.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE));
        return ret.append(keyHint);
    }

    private static void onPartTooltip(ItemTooltipEvent event, ItemStack stack, PartData part) {
        // Type, tier
        event.getToolTip().add(TextUtil.withColor(part.getType().getDisplayName(part.getTier()), Color.AQUAMARINE));

        if (event.getFlags().isAdvanced() && KeyTracker.isControlDown()) {
            event.getToolTip().add(new StringTextComponent("* Part ID: " + part.getPartId()).mergeStyle(TextFormatting.DARK_GRAY));
            event.getToolTip().add(new StringTextComponent("* Part data pack: " + part.getPart().getPackName()).mergeStyle(TextFormatting.DARK_GRAY));
        }

        // Traits
        List<TraitInstance> traits = part.getTraits().stream()
                .filter(inst -> inst.getTrait().showInTooltip(event.getFlags()))
                .collect(Collectors.toList());
        int numTraits = traits.size();
        int traitIndex = getTraitDisplayIndex(numTraits);
        int i = 0;
        for (TraitInstance inst : traits) {
            if (traitIndex < 0 || traitIndex == i) {
                inst.addInformation(event.getToolTip(), event.getFlags());
            }
            ++i;
        }

        if (KeyTracker.isControlDown()) {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.stats")
                    .mergeStyle(TextFormatting.GOLD)
                    .append(new StringTextComponent(" (Silent Gear)")
                            .mergeStyle(TextFormatting.RESET)
                            .mergeStyle(TextFormatting.ITALIC)));
            getPartStatLines(event, stack, part);
        } else {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.ctrlForStats").mergeStyle(TextFormatting.GOLD));
        }

        // Gear type blacklist?
        if (part.getPart() instanceof AbstractGearPart) {
            List<GearType> blacklist = ((AbstractGearPart) part.getPart()).getBlacklistedGearTypes();
            if (!blacklist.isEmpty()) {
                int index = (ClientTicks.ticksInGame() / 20) % blacklist.size();
                GearType gearType = blacklist.get(index);
                ITextComponent blacklistedType = gearType.getDisplayName();
                event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.blacklist", blacklistedType).mergeStyle(TextFormatting.RED));
            }
        }
    }

    private static int getTraitDisplayIndex(int numTraits) {
        if (!TRAIT_DISPLAY_CYCLE || KeyTracker.isControlDown() || numTraits == 0)
            return -1;
        return ClientTicks.ticksInGame() / 20 % numTraits;
    }

    private static void getGradeLine(ItemTooltipEvent event, MaterialGrade grade) {
        ITextComponent text = TextUtil.withColor(grade.getDisplayName(), Color.DEEPSKYBLUE);
        event.getToolTip().add(new TranslationTextComponent("part.silentgear.gradeOnPart", text));
    }

    private static void getMaterialTraitLines(ItemTooltipEvent event, PartType partType, MaterialInstance material) {
        material.getMaterial().getTraits(partType).forEach(t -> {
            event.getToolTip().add(t.getDisplayName());

            // Trait conditions
            if (event.getFlags().isAdvanced()) {
                event.getToolTip().add(new StringTextComponent("  ")
                        .append(TextUtil.withColor(t.getConditionsText(), TextFormatting.DARK_GRAY)));
            }
        });
    }

    private static void getPartStatLines(ItemTooltipEvent event, ItemStack stack, PartData part) {
        TextListBuilder builder = new TextListBuilder();
        for (ItemStat stat : part.getGearType().getRelevantStats()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(ItemStack.EMPTY, stat);
            getStatTooltipLine(event, part.getType(), stat, modifiers).ifPresent(builder::add);
        }
        event.getToolTip().addAll(builder.build());
    }

    private static void getMaterialStatLines(ItemTooltipEvent event, PartType partType, MaterialInstance material) {
        TextListBuilder builder = new TextListBuilder();
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> modifiers = material.getStatModifiers(stat, partType);
            getStatTooltipLine(event, partType, stat, modifiers).ifPresent(builder::add);
        }
        event.getToolTip().addAll(builder.build());
    }

    private static Optional<IFormattableTextComponent> getStatTooltipLine(ItemTooltipEvent event, PartType partType, ItemStat stat, Collection<StatInstance> modifiers) {
        if (!modifiers.isEmpty()) {
            StatInstance inst = stat.computeForDisplay(0, modifiers);
            if (inst.shouldList(partType, stat, event.getFlags().isAdvanced())) {
                boolean isZero = inst.getValue() == 0;
                Color nameColor = isZero ? MC_DARK_GRAY : stat.getNameColor();
                Color statColor = isZero ? MC_DARK_GRAY : Color.WHITE;

                IFormattableTextComponent nameStr = TextUtil.withColor(stat.getDisplayName(), nameColor);
                int decimalPlaces = stat.isDisplayAsInt() && inst.getOp() != StatInstance.Operation.MUL1 && inst.getOp() != StatInstance.Operation.MUL2 ? 0 : 2;
                IFormattableTextComponent statListText = TextUtil.withColor(StatModifierMap.formatText(modifiers, stat, decimalPlaces), statColor);

                // Harvest level hints
                IFormattableTextComponent textWithAdditions = stat == ItemStats.HARVEST_LEVEL && modifiers.size() == 1
                        ? harvestLevelWithHint(statListText, inst.getValue())
                        : statListText;

                return Optional.of(new TranslationTextComponent("stat.silentgear.displayFormat", nameStr, textWithAdditions));
            }
        }

        return Optional.empty();
    }

    public static IFormattableTextComponent harvestLevelWithHint(IFormattableTextComponent statValueText, float statValue) {
        String key = "misc.silentgear.harvestLevel." + Math.round(statValue);
        if (I18n.hasKey(key)) {
            return statValueText.append(TextUtil.misc("spaceBrackets", new TranslationTextComponent(key)));
        }
        return statValueText;
    }
}

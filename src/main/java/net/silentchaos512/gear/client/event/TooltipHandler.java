package net.silentchaos512.gear.client.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
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
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.event.ClientTicks;

import java.util.ArrayList;
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
            return;
        }

        IGearPart part = !stack.isEmpty() ? PartManager.from(stack) : null;
        if (part != null /*&& !part.isBlacklisted(stack)*/) {
            onPartTooltip(event, stack, PartData.of(part, stack));
            return;
        }

        // Nerfed gear?
        if (Config.Server.isNerfedItem(stack.getItem())) {
            List<ITextComponent> toolTip = event.getToolTip();
            toolTip.add(Math.min(1, toolTip.size()), new TranslationTextComponent("misc.silentgear.poorlyMade").func_240699_a_(TextFormatting.RED));
        }
    }

    private static void onMaterialTooltip(ItemTooltipEvent event, ItemStack stack, MaterialInstance material) {
        boolean keyHeld = KeyTracker.isDisplayStatsDown();

        if (keyHeld) {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.material").func_240699_a_(TextFormatting.GOLD));
        } else {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.material")
                    .func_240699_a_(TextFormatting.GOLD)
                    .func_230529_a_(new StringTextComponent(" [")
                            .func_230529_a_(KeyTracker.DISPLAY_STATS.func_238171_j_())
                            .func_230529_a_(new StringTextComponent("]"))
                            .func_240699_a_(TextFormatting.GRAY)));
        }

        if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(new StringTextComponent("Material ID: " + material.getMaterialId()).func_240699_a_(TextFormatting.DARK_GRAY));
        }

        if (keyHeld) {
            getGradeLine(event, material.getGrade());

            List<PartType> partTypes = new ArrayList<>(material.getPartTypes());
            if (!partTypes.isEmpty()) {
                int index = KeyTracker.getMaterialCycleCount() % partTypes.size();
                PartType partType = partTypes.get(index);
                event.getToolTip().add(buildPartTypeHeader(partTypes, partType));
                event.getToolTip().add(TextUtil.misc("tier", material.getTier(partType)));

                getMaterialTraitLines(event, partType, material);

                event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.stats").func_240699_a_(TextFormatting.GOLD));
                getMaterialStatLines(event, partType, material);
            }
        } else {
            if (material.getGrade() != MaterialGrade.NONE) {
                getGradeLine(event, material.getGrade());
            }
        }
    }

    private static ITextComponent buildPartTypeHeader(Collection<PartType> types, PartType selectedType) {
        IFormattableTextComponent ret = new StringTextComponent("| ").func_240699_a_(TextFormatting.GRAY);
        for (PartType type : types) {
            ITextComponent text = type.getDisplayName(-1);
            text.getStyle().setFormatting(type == selectedType ? TextFormatting.WHITE : TextFormatting.DARK_GRAY);
            ret.func_230529_a_(text).func_240702_b_(" | ");
        }

        IFormattableTextComponent keyHint = new StringTextComponent("[")
                .func_230529_a_(KeyTracker.CYCLE_MATERIAL_INFO.func_238171_j_())
                .func_230529_a_(new StringTextComponent("]"));
        return ret.func_230529_a_(keyHint.func_240699_a_(TextFormatting.AQUA));
    }

    private static void onPartTooltip(ItemTooltipEvent event, ItemStack stack, PartData partData) {
        IGearPart part = partData.getPart();

        // Type, tier
        ITextComponent typeName = part.getType().getDisplayName(partData.getTier());
        typeName.getStyle().setColor(Color.func_240743_a_(0x7FFFD4));
        event.getToolTip().add(typeName);

        if (event.getFlags().isAdvanced() && KeyTracker.isControlDown()) {
            event.getToolTip().add(new StringTextComponent("* Part ID: " + part.getId()).func_240699_a_(TextFormatting.DARK_GRAY));
            event.getToolTip().add(new StringTextComponent("* Part data pack: " + part.getPackName()).func_240699_a_(TextFormatting.DARK_GRAY));
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
                    .func_240699_a_(TextFormatting.GOLD)
                    .func_230529_a_(new StringTextComponent(" (Silent Gear)")
                            .func_240699_a_(TextFormatting.RESET)
                            .func_240699_a_(TextFormatting.ITALIC)));
            getPartStatLines(event, stack, part);
        } else {
            event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.ctrlForStats").func_240699_a_(TextFormatting.GOLD));
        }

        // Gear type blacklist?
        if (part instanceof AbstractGearPart) {
            List<GearType> blacklist = ((AbstractGearPart) part).getBlacklistedGearTypes();
            if (!blacklist.isEmpty()) {
                int index = (ClientTicks.ticksInGame() / 20) % blacklist.size();
                GearType gearType = blacklist.get(index);
                ITextComponent blacklistedType = gearType.getDisplayName();
                event.getToolTip().add(new TranslationTextComponent("misc.silentgear.tooltip.blacklist", blacklistedType).func_240699_a_(TextFormatting.RED));
            }
        }
    }

    private static int getTraitDisplayIndex(int numTraits) {
        if (!TRAIT_DISPLAY_CYCLE || KeyTracker.isControlDown() || numTraits == 0)
            return -1;
        return ClientTicks.ticksInGame() / 20 % numTraits;
    }

    private static void getGradeLine(ItemTooltipEvent event, MaterialGrade grade) {
        ITextComponent text = grade.getDisplayName();
        text.getStyle().setColor(Color.func_240743_a_(0x00BFFF));
        event.getToolTip().add(new TranslationTextComponent("part.silentgear.gradeOnPart", text));
    }

    private static void getMaterialTraitLines(ItemTooltipEvent event, PartType partType, MaterialInstance material) {
        material.getMaterial().getTraits(partType).forEach(t -> event.getToolTip().add(t.getDisplayName()));
    }

    private static void getPartStatLines(ItemTooltipEvent event, ItemStack stack, IGearPart part) {
        PartData partData = PartData.of(part, stack);
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> modifiers = part.getStatModifiers(stat, partData);
            getStatTooltipLine(event, part.getType(), stat, modifiers);
        }
    }

    private static void getMaterialStatLines(ItemTooltipEvent event, PartType partType, MaterialInstance material) {
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> modifiers = material.getStatModifiers(stat, partType);
            getStatTooltipLine(event, partType, stat, modifiers);
        }
    }

    private static void getStatTooltipLine(ItemTooltipEvent event, PartType partType, ItemStat stat, Collection<StatInstance> modifiers) {
        if (!modifiers.isEmpty()) {
            StatInstance inst = stat.computeForDisplay(0, modifiers);
            if (inst.shouldList(partType, stat, event.getFlags().isAdvanced())) {
                boolean isZero = inst.getValue() == 0;
                TextFormatting nameColor = isZero ? TextFormatting.DARK_GRAY : stat.getNameColor();
                TextFormatting statColor = isZero ? TextFormatting.DARK_GRAY : TextFormatting.WHITE;
                ITextComponent nameStr = stat.getDisplayName();
                nameStr.getStyle().setFormatting(nameColor);
                int decimalPlaces = stat.isDisplayAsInt() && inst.getOp() != StatInstance.Operation.MUL1 && inst.getOp() != StatInstance.Operation.MUL2 ? 0 : 2;

                ITextComponent statListText = StatModifierMap.formatText(modifiers, stat, decimalPlaces);
                statListText.getStyle().setFormatting(statColor);

                event.getToolTip().add(new StringTextComponent("- ").func_240699_a_(TextFormatting.GRAY)
                        .func_230529_a_(new TranslationTextComponent("stat.silentgear.displayFormat", nameStr, statListText.getString())));
            }
        }
    }
}

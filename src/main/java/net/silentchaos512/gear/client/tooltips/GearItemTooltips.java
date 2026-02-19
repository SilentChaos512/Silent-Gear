package net.silentchaos512.gear.client.tooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.util.GearTooltipFlag;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber
public class GearItemTooltips {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof GearItem item)) return;

        var flag = event.getFlags() instanceof GearTooltipFlag
                ? (GearTooltipFlag) event.getFlags()
                : GearTooltipFlag.withModifierKeys(event.getFlags().isAdvanced(), true, true);

        List<Component> newLines = new ArrayList<>();

        if (GearHelper.isBroken(stack)) {
            newLines.add(TextUtil.withColor(misc("broken"), Color.FIREBRICK));
        }

        if (GearData.isExampleGear(stack)) {
            newLines.add(TextUtil.withColor(misc("exampleOutput1"), Color.YELLOW));
            newLines.add(TextUtil.withColor(misc("exampleOutput2"), Color.YELLOW));
        }

        PartList constructionParts = GearData.getConstruction(stack).parts();
        List<PartInstance> sortedConstructionParts = constructionParts.toSortedList();

        if (constructionParts.getMains().isEmpty()) {
            newLines.add(TextUtil.withColor(misc("invalidParts"), Color.FIREBRICK));
        }

        if (!Config.Client.vanillaStyleTooltips.get()) {
            // Let parts add information if they need to
            for (PartInstance part : sortedConstructionParts) {
                part.addInformation(stack, newLines, flag);
            }
        }

        if (!Config.Client.vanillaStyleTooltips.get()) {
            // Properties
            addPropertiesInfo(stack, newLines, flag, item);
        }

        // Tool construction
        if (KeyTracker.isDisplayConstructionDown() && flag.showConstruction()) {
            newLines.add(TextUtil.withColor(misc("tooltip.construction"), Color.GOLD));
            tooltipListParts(stack, newLines, sortedConstructionParts, flag);
        } else if (flag.showConstruction()) {
            newLines.add(TextUtil.withColor(TextUtil.misc("tooltip.construction"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_CONSTRUCTION), ChatFormatting.GRAY))));
        }

        // Add all lines near top of tooltip
        event.getToolTip().addAll(Math.min(1, event.getToolTip().size()), newLines);
    }

    private static MutableComponent misc(String key, Object... formatArgs) {
        return Component.translatable("misc.silentgear." + key, formatArgs);
    }

    public static void addPropertiesInfo(ItemStack stack, List<Component> tooltip, GearTooltipFlag flag, GearItem item) {
        if (KeyTracker.isDisplayPropertiesDown() && flag.showProperties()) {
            tooltip.add(TextUtil.withColor(misc("tooltip.properties"), Color.GOLD));

            TextListBuilder builder = new TextListBuilder();
            var gearProperties = GearData.getProperties(stack);

            for (GearProperty<?, ?> property : getDisplayProperties(stack, flag)) {
                GearPropertyValue<?> value = gearProperties.get(property);
                if (value != null && !property.isHiddenUnchecked(value, flag)) {
                    property.buildTooltipUnchecked(builder, value, stack, flag, FormatColorScheme.NO_COLORS);
                }
            }

            tooltip.addAll(builder.build());
        } else if (flag.showProperties()) {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.properties"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_PROPERTIES), ChatFormatting.GRAY))));
        }
    }

    private static Iterable<GearProperty<?, ?>> getDisplayProperties(ItemStack stack, GearTooltipFlag flag) {
        if (flag.isAdvanced() && SilentGear.isDevBuild()) {
            return SgRegistries.GEAR_PROPERTY;
        }
        return GearPropertyGroups.getSortedRelevantProperties(GearHelper.getType(stack).relevantPropertyGroups());
    }

    public static void tooltipListParts(ItemStack gear, List<Component> tooltip, Collection<PartInstance> parts, GearTooltipFlag flag) {
        TextListBuilder builder = new TextListBuilder();

        for (PartInstance part : parts) {
            if (part.isValid() && part.get().isVisible()) {
                int partNameColor = Color.blend(part.getColor(gear), Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
                MutableComponent partNameText = TextUtil.withColor(part.getDisplayName().copy(), partNameColor);
                builder.add(flag.isAdvanced()
                        ? partNameText.append(TextUtil.misc("spaceBrackets", part.getType().getDisplayName()).withStyle(ChatFormatting.DARK_GRAY))
                        : partNameText);

                // List materials for compound parts
                if (part.get() instanceof CoreGearPart) {
                    builder.indent();
                    for (MaterialInstance material : CompoundPartItem.getMaterials(part.getItem())) {
                        int nameColor = material.getNameColor(part.getType(), GearTypes.ALL.get());
                        builder.add(TextUtil.withColor(material.getDisplayNameWithModifiers(part.getType(), ItemStack.EMPTY), nameColor));
                    }
                    builder.unindent();
                }
            }
        }

        tooltip.addAll(builder.build());
    }
}

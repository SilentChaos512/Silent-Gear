package net.silentchaos512.gear.client.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class GearClientHelper {
    private GearClientHelper() {
    }

    public static int getColor(ItemStack stack, PartType layer) {
        PartInstance part = GearData.getPartOfType(stack, layer);
        if (part != null) {
            return part.getColor(stack, 0, 0);
        }
        return Color.VALUE_WHITE;
    }

    public static void addInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        GearTooltipFlag flagTC = flag instanceof GearTooltipFlag
                ? (GearTooltipFlag) flag
                : GearTooltipFlag.withModifierKeys(flag.isAdvanced(), true, true);
        addInformation(stack, context, tooltip, flagTC);
    }

    public static void addInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, GearTooltipFlag flag) {
        if (!(stack.getItem() instanceof GearItem item)) return;

        if (GearHelper.isBroken(stack)) {
            tooltip.add(Math.min(1, tooltip.size()), TextUtil.withColor(misc("broken"), Color.FIREBRICK));
        }

        if (GearData.isExampleGear(stack)) {
            tooltip.add(Math.min(1, tooltip.size()), TextUtil.withColor(misc("exampleOutput1"), Color.YELLOW));
            tooltip.add(Math.min(2, tooltip.size()), TextUtil.withColor(misc("exampleOutput2"), Color.YELLOW));
        }

        PartList constructionParts = GearData.getConstruction(stack).parts();

        if (constructionParts.getMains().isEmpty()) {
            tooltip.add(TextUtil.withColor(misc("invalidParts"), Color.FIREBRICK));
        }

        if (!Config.Client.vanillaStyleTooltips.get()) {
            // Let parts add information if they need to
            //Collections.reverse(constructionParts);
            for (PartInstance data : constructionParts) {
                data.get().addInformation(data, stack, tooltip, flag);
            }
        }

        if (!Config.Client.vanillaStyleTooltips.get()) {
            // Properties
            addStatsInfo(stack, tooltip, flag, item);
        }

        // Tool construction
        if (KeyTracker.isDisplayConstructionDown() && flag.showConstruction) {
            tooltip.add(TextUtil.withColor(misc("tooltip.construction"), Color.GOLD));
            Collections.reverse(constructionParts);
            tooltipListParts(stack, tooltip, constructionParts, flag);
        } else if (flag.showConstruction) {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.construction"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_CONSTRUCTION), ChatFormatting.GRAY))));
        }
    }

    public static void addStatsInfo(ItemStack stack, List<Component> tooltip, GearTooltipFlag flag, GearItem item) {
        if (KeyTracker.isDisplayStatsDown() && flag.showStats) {
            tooltip.add(TextUtil.withColor(misc("tooltip.properties"), Color.GOLD));

            TextListBuilder builder = new TextListBuilder();
            var gearProperties = GearData.getProperties(stack);

            for (GearProperty<?, ?> property : getDisplayProperties(stack, flag)) {
                if (property == GearProperties.ENCHANTMENT_VALUE && !Config.Common.allowEnchanting.get()) {
                    // Enchanting not allowed, so hide the stat
                    continue;
                }

                GearPropertyValue<?> value = gearProperties.get(property);
                if (value == null) continue;

                if (property == GearProperties.DURABILITY) {
                    // Durability-specific formatting
                    int durabilityLeft = stack.getMaxDamage() - stack.getDamageValue();
                    int durabilityMax = stack.getMaxDamage();
                    var text = statText("durabilityFormat", durabilityLeft, durabilityMax);
                    builder.add(property.formatText(text));
                } else {
                    // All other properties
                    property.getTooltipLinesUnchecked(value, flag).forEach(builder::add);
                }
            }

            tooltip.addAll(builder.build());
        } else if (flag.showStats) {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.properties"), Color.GOLD)
                    .append(Component.literal(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_STATS), ChatFormatting.GRAY))));
        }
    }

    private static Iterable<GearProperty<?, ?>> getDisplayProperties(ItemStack stack, GearTooltipFlag flag) {
        if (flag.isAdvanced() && SilentGear.isDevBuild()) {
            return SgRegistries.GEAR_PROPERTY;
        }
        return GearPropertyGroups.getSortedRelevantProperties(GearHelper.getType(stack).relevantPropertyGroups());
    }

    private static MutableComponent misc(String key, Object... formatArgs) {
        return Component.translatable("misc.silentgear." + key, formatArgs);
    }

    private static MutableComponent statText(String key, Object... formatArgs) {
        return Component.translatable("property.silentgear." + key, formatArgs);
    }

    public static void tooltipListParts(ItemStack gear, List<Component> tooltip, Collection<PartInstance> parts, GearTooltipFlag flag) {
        TextListBuilder builder = new TextListBuilder();

        for (PartInstance part : parts) {
            if (part.get().isVisible()) {
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

    public static boolean hasEffect(ItemStack stack) {
        return Config.Client.allowEnchantedEffect.get() && stack.isEnchanted();
    }

    public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack);
    }
}

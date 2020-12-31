package net.silentchaos512.gear.client.util;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.utils.Color;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public final class GearClientHelper {
    private GearClientHelper() {
    }

    public static int getColor(ItemStack stack, PartType layer) {
        PartData part = GearData.getPartOfType(stack, layer);
        if (part != null) {
            return part.getColor(stack, 0, 0);
        }
        return Color.VALUE_WHITE;
    }

    public static void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        GearTooltipFlag flagTC = flag instanceof GearTooltipFlag
                ? (GearTooltipFlag) flag
                : GearTooltipFlag.withModifierKeys(flag.isAdvanced(), true, true);
        addInformation(stack, world, tooltip, flagTC);
    }

    public static void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, GearTooltipFlag flag) {
        /*
        LoaderState state = Loader.instance().getLoaderState();
        if (state == LoaderState.INITIALIZATION || state == LoaderState.SERVER_ABOUT_TO_START || state == LoaderState.SERVER_STOPPING) {
            // Skip tooltips during block/item remapping
            // JEI tooltip caches are done in AVAILABLE, in-game is SERVER_STARTED
            return;
        }
        */

        if (!(stack.getItem() instanceof ICoreItem)) return;

        ICoreItem item = (ICoreItem) stack.getItem();

        if (GearHelper.isBroken(stack)) {
            tooltip.add(Math.min(1, tooltip.size()), TextUtil.withColor(misc("broken"), Color.FIREBRICK));
        }

        if (GearData.isExampleGear(stack)) {
            tooltip.add(Math.min(1, tooltip.size()), TextUtil.withColor(misc("exampleOutput1"), Color.YELLOW));
            tooltip.add(Math.min(2, tooltip.size()), TextUtil.withColor(misc("exampleOutput2"), Color.YELLOW));
        }

        PartDataList constructionParts = GearData.getConstructionParts(stack);

        if (constructionParts.getMains().isEmpty()) {
            tooltip.add(TextUtil.withColor(misc("invalidParts"), Color.FIREBRICK));
            tooltip.add(TextUtil.withColor(misc("lockedStats"), Color.FIREBRICK));
        } else if (GearData.hasLockedStats(stack)) {
            tooltip.add(TextUtil.withColor(misc("lockedStats"), Color.YELLOW));
        }

        // Let parts add information if they need to
        Collections.reverse(constructionParts);
        for (PartData data : constructionParts) {
            data.getPart().addInformation(data, stack, tooltip, flag);
        }

        // Traits
        addTraitsInfo(stack, tooltip, flag);

        // Stats
        addStatsInfo(stack, tooltip, flag, item);

        // Tool construction
        if (KeyTracker.isDisplayConstructionDown() && flag.showConstruction) {
            tooltip.add(TextUtil.withColor(misc("tooltip.construction"), Color.GOLD));
            Collections.reverse(constructionParts);
            tooltipListParts(stack, tooltip, constructionParts, flag);
        } else if (flag.showConstruction) {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.construction"), Color.GOLD)
                    .append(new StringTextComponent(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_CONSTRUCTION), TextFormatting.GRAY))));
        }
    }

    public static void addStatsInfo(ItemStack stack, List<ITextComponent> tooltip, GearTooltipFlag flag, ICoreItem item) {
        if (KeyTracker.isDisplayStatsDown() && flag.showStats) {
            tooltip.add(TextUtil.withColor(misc("tooltip.stats"), Color.GOLD));

            tooltip.add(TextUtil.withColor(misc("tier", GearData.getTier(stack)), Color.DEEPSKYBLUE));

            // Display only stats relevant to the item class
            Collection<ItemStat> relevantStats = item.getRelevantStats(stack);
            Collection<ItemStat> displayStats = flag.isAdvanced() && SilentGear.isDevBuild() ? ItemStats.allStatsOrdered() : relevantStats;

            TextListBuilder builder = new TextListBuilder();

            for (ItemStat stat : displayStats) {
                float statValue = GearData.getStat(stack, stat);

                // Used for the total armor/toughness a full suit of armor would provide
                float totalArmor = -1;
                if (item instanceof CoreArmor) {
                    if (stat == ItemStats.ARMOR) {
                        // Armor value varies by type
                        totalArmor = statValue;
                        statValue = (float) ((CoreArmor) item).getArmorProtection(stack);
                    } else if (stat == ItemStats.MAGIC_ARMOR) {
                        // Same as armor
                        totalArmor = statValue;
                        statValue = (float) ((CoreArmor) item).getArmorMagicProtection(stack);
                    } else if (stat == ItemStats.ARMOR_TOUGHNESS) {
                        // Toughness split equally to each piece
                        totalArmor = statValue;
                        statValue /= 4;
                    }
                }

                StatInstance inst = StatInstance.of(statValue);
                Color nameColor = relevantStats.contains(stat) ? stat.getNameColor() : TooltipHandler.MC_DARK_GRAY;
                ITextComponent textName = TextUtil.withColor(stat.getDisplayName(), nameColor);
                IFormattableTextComponent textStat = inst.getFormattedText(stat, stat.isDisplayAsInt() ? 0 : 2, false);

                // Some stat-specific formatting...
                // TODO: The stats should probably handle this instead
                if (stat == ItemStats.DURABILITY) {
                    int durabilityLeft = stack.getMaxDamage() - stack.getDamage();
                    int durabilityMax = stack.getMaxDamage();
                    textStat = statText("durabilityFormat", durabilityLeft, durabilityMax);
                } else if (stat == ItemStats.HARVEST_LEVEL) {
                    textStat = TooltipHandler.harvestLevelWithHint(textStat, statValue);
                }

                builder.add(statText("displayFormat", textName, textStat));
            }

            tooltip.addAll(builder.build());
        } else if (flag.showStats) {
            tooltip.add(TextUtil.withColor(TextUtil.misc("tooltip.stats"), Color.GOLD)
                    .append(new StringTextComponent(" ")
                            .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_STATS), TextFormatting.GRAY))));
        }
    }

    private static void addTraitsInfo(ItemStack stack, List<ITextComponent> tooltip, GearTooltipFlag flag) {
        Map<ITrait, Integer> traits = TraitHelper.getCachedTraits(stack);
        List<ITrait> visibleTraits = new ArrayList<>();
        for (ITrait t : traits.keySet()) {
            if (t != null && t.showInTooltip(flag)) {
                visibleTraits.add(t);
            }
        }

        int traitIndex = getTraitDisplayIndex(visibleTraits.size(), flag);
        IFormattableTextComponent textTraits = TextUtil.withColor(misc("tooltip.traits"), Color.GOLD);
        if (traitIndex < 0) {
            tooltip.add(textTraits);
        }

        int i = 0;
        for (ITrait trait : visibleTraits) {
            if (traitIndex < 0 || traitIndex == i) {
                final int level = traits.get(trait);
                trait.addInformation(level, tooltip, flag, text -> {
                    if (traitIndex >= 0) {
                        return textTraits
                                .append(TextUtil.withColor(new StringTextComponent(": "), TextFormatting.GRAY)
                                        .append(text));
                    }
                    return new StringTextComponent(TextListBuilder.BULLETS[0] + " ").append(text);
                });
            }
            ++i;
        }
    }

    private static int getTraitDisplayIndex(int numTraits, GearTooltipFlag flag) {
        if (KeyTracker.isDisplayTraitsDown() || numTraits == 0)
            return -1;
        return ClientTicks.ticksInGame() / 20 % numTraits;
    }

    private static IFormattableTextComponent misc(String key, Object... formatArgs) {
        return new TranslationTextComponent("misc.silentgear." + key, formatArgs);
    }

    private static IFormattableTextComponent statText(String key, Object... formatArgs) {
        return new TranslationTextComponent("stat.silentgear." + key, formatArgs);
    }

    public static void tooltipListParts(ItemStack gear, List<ITextComponent> tooltip, Collection<PartData> parts, GearTooltipFlag flag) {
        TextListBuilder builder = new TextListBuilder();

        for (PartData part : parts) {
            if (part.getPart().isVisible()) {
                int partNameColor = Color.blend(part.getColor(gear), Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
                IFormattableTextComponent partNameText = TextUtil.withColor(part.getDisplayName(gear).deepCopy(), partNameColor);
                builder.add(flag.isAdvanced()
                        ? partNameText.append(TextUtil.misc("spaceBrackets", part.getType().getName()).mergeStyle(TextFormatting.DARK_GRAY))
                        : partNameText);

                // List materials for compound parts
                if (part.getPart() instanceof CompoundPart) {
                    builder.indent();
                    for (MaterialInstance material : CompoundPartItem.getMaterials(part.getCraftingItem())) {
                        int nameColor = material.getMaterial().getNameColor(part.getType(), gear);
                        builder.add(TextUtil.withColor(material.getDisplayNameWithGrade(part.getType()), nameColor));
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

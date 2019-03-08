package net.silentchaos512.gear.client.util;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public final class GearClientHelper {
    public static Map<String, IBakedModel> modelCache = new HashMap<>();

    private GearClientHelper() {}

    public static void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        TooltipFlagTC flagTC = flag instanceof TooltipFlagTC
                ? (TooltipFlagTC) flag
                : TooltipFlagTC.withModifierKeys(flag.isAdvanced(), true, true);
        addInformation(stack, world, tooltip, flagTC);
    }

    public static void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, TooltipFlagTC flag) {
        /*
        LoaderState state = Loader.instance().getLoaderState();
        if (state == LoaderState.INITIALIZATION || state == LoaderState.SERVER_ABOUT_TO_START || state == LoaderState.SERVER_STOPPING) {
            // Skip tooltips during block/item remapping
            // JEI tooltip caches are done in AVAILABLE, in-game is SERVER_STARTED
            return;
        }
        */

        if (!(stack.getItem() instanceof ICoreItem)) return;

        boolean ctrlDown = flag.ctrlDown;
        boolean altDown = flag.altDown;

        ICoreItem item = (ICoreItem) stack.getItem();

        if (GearHelper.isBroken(stack)) {
            tooltip.add(1, misc("broken").applyTextStyle(TextFormatting.RED));
        }

        if (GearData.isExampleGear(stack)) {
            tooltip.add(1, misc("exampleOutput1").applyTextStyle(TextFormatting.YELLOW));
            tooltip.add(2, misc("exampleOutput2").applyTextStyle(TextFormatting.YELLOW));
        }

        PartDataList constructionParts = GearData.getConstructionParts(stack);

        if (constructionParts.getMains().isEmpty()) {
            tooltip.add(misc("invalidParts").applyTextStyle(TextFormatting.RED));
            tooltip.add(misc("lockedStats").applyTextStyle(TextFormatting.RED));
        } else if (GearData.hasLockedStats(stack)) {
            tooltip.add(misc("lockedStats").applyTextStyle(TextFormatting.YELLOW));
        } else {
            if (constructionParts.getRods().isEmpty()) {
                tooltip.add(misc("missingRod").applyTextStyle(TextFormatting.RED));
            }
            if (item.requiresPartOfType(PartType.BOWSTRING) && constructionParts.getPartsOfType(PartType.BOWSTRING).isEmpty()) {
                tooltip.add(misc("missingBowstring").applyTextStyle(TextFormatting.RED));
            }
        }

        // Let parts add information if they need to
        Collections.reverse(constructionParts);
        for (PartData data : constructionParts) {
            data.getPart().addInformation(data, stack, tooltip, flag);
        }

        TraitHelper.getTraits(constructionParts).forEach((trait, level) ->
                tooltip.add(trait.getDisplayName(level)));

        float synergyDisplayValue = GearData.getSynergyDisplayValue(stack);
        TextFormatting color = synergyDisplayValue < 1 ? TextFormatting.RED : synergyDisplayValue > 1 ? TextFormatting.GREEN : TextFormatting.WHITE;
        tooltip.add(new TextComponentString("Synergy: " + color + String.format("%d%%", (int) (100 * synergyDisplayValue))));

        if (flag.isAdvanced()) {
            // ICoreTool itemTool = (ICoreTool) item;
            // tooltip.add(itemTool.getGearClass());
            NBTTagCompound tagCompound = stack.getOrCreateTag();
            if (tagCompound.hasKey("debug_modelkey")) {
                tooltip.add(new TextComponentString(tagCompound.getString("debug_modelkey")).applyTextStyle(TextFormatting.DARK_GRAY));
            }
        }

        // Stats!
        ITextComponent textStats = misc("tooltip.stats").applyTextStyle(TextFormatting.GOLD);
        if (ctrlDown && flag.showStats) {
            tooltip.add(textStats);
            // Display only stats relevant to the item class
            Collection<ItemStat> relevantStats = flag.isAdvanced() && SilentGear.isDevBuild()
                    ? ItemStat.ALL_STATS.values()
                    : item.getRelevantStats(stack);
            for (ItemStat stat : relevantStats) {
                float statValue = GearData.getStat(stack, stat);

                // Used for the total armor/toughness a full suit of armor would provide
                float totalArmor = -1;
                if (item instanceof CoreArmor) {
                    if (stat == CommonItemStats.ARMOR) {
                        // Armor value varies by type
                        totalArmor = statValue;
                        statValue = (float) ((CoreArmor) item).getArmorProtection(stack);
                    } else if (stat == CommonItemStats.ARMOR_TOUGHNESS) {
                        // Toughness split equally to each piece
                        totalArmor = statValue;
                        statValue /= 4;
                    }
                }

                StatInstance inst = new StatInstance("display_" + stat.getName(), statValue, StatInstance.Operation.AVG);
                ITextComponent textName = new TextComponentString("- ").appendSibling(stat.getDisplayName());
                ITextComponent textStat = new TextComponentString(inst.formattedString(stat.displayAsInt ? 0 : 1, false));

                // Some stat-specific formatting...
                if (stat == CommonItemStats.DURABILITY) {
                    int durabilityLeft = stack.getMaxDamage() - stack.getDamage();
                    int durabilityMax = stack.getMaxDamage();
                    textStat = statText("durabilityFormat", durabilityLeft, durabilityMax);
                } else if (stat == CommonItemStats.ARMOR || stat == CommonItemStats.ARMOR_TOUGHNESS) {
                    String str1 = String.format("%.1f", statValue);
                    String str2 = String.format("%.1f", totalArmor);
                    textStat = statText("armorFormat", str1, str2);
                }

                tooltip.add(statText("displayFormat", textName, textStat));
            }
        } else if (flag.showStats) {
            textStats.appendText(" ").appendSibling(misc("tooltip.stats.key").applyTextStyle(TextFormatting.GRAY));
            tooltip.add(textStats);
        }

        // Tool construction
        ITextComponent textConstruction = misc("tooltip.construction").applyTextStyle(TextFormatting.GOLD);
        if (altDown && flag.showConstruction) {
            tooltip.add(textConstruction);
            Collections.reverse(constructionParts);
            tooltipListParts(stack, tooltip, constructionParts);
        } else if (flag.showConstruction) {
            textConstruction.appendSibling(new TextComponentString(" ")
                    .applyTextStyle(TextFormatting.GRAY)
                    .appendSibling(misc("tooltip.construction.key")));
            tooltip.add(textConstruction);
        }
    }

    private static ITextComponent misc(String key, Object... formatArgs) {
        return new TextComponentTranslation("misc.silentgear." + key, formatArgs);
    }

    private static ITextComponent statText(String key, Object... formatArgs) {
        return new TextComponentTranslation("stat.silentgear." + key, formatArgs);
    }

    public static void tooltipListParts(ItemStack gear, List<ITextComponent> tooltip, Collection<PartData> parts) {
        for (PartData part : parts) {
            ITextComponent text = new TextComponentString("- ").appendSibling(part.getDisplayName(gear));
            if (part.getPart().getType() == PartType.MAIN) {
                ITextComponent gradeText = new TextComponentString(" (")
                        .applyTextStyle(TextFormatting.RESET)
                        .appendSibling(part.getGrade().getDisplayName())
                        .appendText(")");
                text.appendSibling(gradeText);
            }
            tooltip.add(text);
        }
    }

    public static boolean hasEffect(ItemStack stack) {
        return stack.isEnchanted();
    }

    public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack);
    }

    public static Map<String, PartData> getRenderParts(ItemStack stack) {
        Map<String, PartData> map = new LinkedHashMap<>();

        ICoreTool item = (ICoreTool) stack.getItem();
        GearType itemClass = item.getGearType();
        boolean hasGuard = item.hasSwordGuard();

        PartData partHead = item.getPrimaryPart(stack);
        PartData partGuard = hasGuard ? item.getSecondaryPart(stack) : null;
        PartData partRod = item.getRodPart(stack);
        PartData partTip = item.getTipPart(stack);
        PartData partBowstring = item.getBowstringPart(stack);

        if (partRod != null)
            map.put("rod", partRod);
        if (partHead != null)
            map.put("head", partHead);
        if (partGuard != null)
            map.put("guard", partGuard);
        if (partTip != null)
            map.put("tip", partTip);
        if (partBowstring != null)
            map.put("bowstring", partBowstring);

        return map;
    }
}

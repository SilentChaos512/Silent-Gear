package net.silentchaos512.gear.client.util;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.StackHelper;

import java.util.*;

@SideOnly(Side.CLIENT)
public class GearClientHelper {
    public static Map<String, IBakedModel> modelCache = new HashMap<>();

    public static void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        TooltipFlagTC flagTC = flag instanceof TooltipFlagTC ? (TooltipFlagTC) flag
                : TooltipFlagTC.withModifierKeys(flag.isAdvanced(), true, true);
        addInformation(stack, world, tooltip, flagTC);
    }

    public static void addInformation(ItemStack stack, World world, List<String> tooltip, TooltipFlagTC flag) {
        LoaderState state = Loader.instance().getLoaderState();
        if (state == LoaderState.INITIALIZATION || state == LoaderState.SERVER_ABOUT_TO_START || state == LoaderState.SERVER_STOPPING) {
            // Skip tooltips during block/item remapping
            // JEI tooltip caches are done in AVAILABLE, in-game is SERVER_STARTED
            return;
        }

        if (stack.getItem() instanceof ICoreItem) {
            boolean ctrlDown = flag.ctrlDown;
            boolean altDown = flag.altDown;

            ICoreItem item = (ICoreItem) stack.getItem();

            if (GearHelper.isBroken(stack)) {
                tooltip.add(1, TextFormatting.RED + SilentGear.i18n.translate("misc", "broken"));
            }

            if (GearData.isExampleGear(stack)) {
                tooltip.add(1, TextFormatting.YELLOW + SilentGear.i18n.translate("misc", "exampleOutput1"));
                tooltip.add(2, TextFormatting.YELLOW + SilentGear.i18n.translate("misc", "exampleOutput2"));
            }

            PartDataList constructionParts = GearData.getConstructionParts(stack);

            if (constructionParts.getMains().isEmpty()) {
                tooltip.add(TextFormatting.RED + SilentGear.i18n.translate("misc", "invalidParts"));
                tooltip.add(TextFormatting.RED + SilentGear.i18n.translate("misc", "lockedStats"));
            } else if (GearData.hasLockedStats(stack)) {
                tooltip.add(TextFormatting.YELLOW + SilentGear.i18n.translate("misc", "lockedStats"));
            }

            // Let parts add information if they need to
            Collections.reverse(constructionParts);
            for (ItemPartData data : constructionParts) {
                data.getPart().addInformation(data, stack, world, tooltip, flag.isAdvanced());
            }

            TraitHelper.getTraits(constructionParts).forEach((trait, level) ->
                    tooltip.add(trait.getNameColor() + trait.getTranslatedName(level)));

            float synergyDisplayValue = GearData.getSynergyDisplayValue(stack);
            TextFormatting color = synergyDisplayValue < 1 ? TextFormatting.RED : synergyDisplayValue > 1 ? TextFormatting.GREEN : TextFormatting.WHITE;
            tooltip.add("Synergy: " + color + String.format("%d%%", (int) (100 * synergyDisplayValue)));

            if (flag.isAdvanced()) {
                // ICoreTool itemTool = (ICoreTool) item;
                // tooltip.add(itemTool.getGearClass());
                NBTTagCompound tagCompound = StackHelper.getTagCompound(stack, true);
                if (tagCompound.hasKey("debug_modelkey")) {
                    tooltip.add(TextFormatting.DARK_GRAY + tagCompound.getString("debug_modelkey"));
                }
            }

            // Stats!
            String strStats = TextFormatting.GOLD + SilentGear.i18n.translate("misc", "tooltip.stats.name");
            if (ctrlDown && flag.showStats) {
                tooltip.add(strStats);
                // Display only stats relevant to the item class
                for (ItemStat stat : item.getRelevantStats(stack)) {
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
                    String nameStr = "- " + stat.displayColor + SilentGear.i18n.translate("stat." + stat.getName());
                    String statStr = inst.formattedString(stat.displayAsInt ? 0 : 1, false);

                    // Some stat-specific formatting...
                    if (stat == CommonItemStats.DURABILITY) {
                        int durabilityLeft = stack.getMaxDamage() - stack.getItemDamage();
                        int durabilityMax = stack.getMaxDamage();
                        statStr = SilentGear.i18n.translate("stat", "durabilityFormat", durabilityLeft, durabilityMax);
                    } else if (stat == CommonItemStats.ARMOR || stat == CommonItemStats.ARMOR_TOUGHNESS) {
                        String str1 = String.format("%.1f", statValue);
                        String str2 = String.format("%.1f", totalArmor);
                        statStr = SilentGear.i18n.translate("stat", "armorFormat", str1, str2);
                    }

                    tooltip.add(SilentGear.i18n.translate("stat", "displayFormat", nameStr, statStr));
                }
            } else if (flag.showStats) {
                strStats += " " + TextFormatting.GRAY + SilentGear.i18n.translate("misc", "tooltip.stats.key");
                tooltip.add(strStats);
            }

            // Tool construction
            String strConstruction = TextFormatting.GOLD + SilentGear.i18n.translate("misc", "tooltip.construction.name");
            if (altDown && flag.showConstruction) {
                tooltip.add(strConstruction);
                Collections.reverse(constructionParts);
                tooltipListParts(stack, tooltip, constructionParts);
            } else if (flag.showConstruction) {
                strConstruction += " " + TextFormatting.GRAY + SilentGear.i18n.translate("misc", "tooltip.construction.key");
                tooltip.add(strConstruction);
            }
        }
    }

    public static void tooltipListParts(ItemStack gear, List<String> tooltip, Collection<ItemPartData> parts) {
        for (ItemPartData part : parts) {
            String str = "- " + part.getNameColor() + part.getTranslatedName(gear);
            if (part.getPart() instanceof PartMain)
                str += TextFormatting.RESET + " (" + part.getGrade().getTranslatedName() + ")";
            tooltip.add(str);
        }
    }

    public static boolean hasEffect(ItemStack stack) {
        return stack.isItemEnchanted();
    }

    public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack);
    }

    public static Map<String, ItemPartData> getRenderParts(ItemStack stack) {
        Map<String, ItemPartData> map = new LinkedHashMap<>();

        ICoreTool item = (ICoreTool) stack.getItem();
        String itemClass = item.getGearClass();
        boolean hasGuard = "sword".equals(itemClass);

        ItemPartData partHead = item.getPrimaryPart(stack);
        ItemPartData partGuard = hasGuard ? item.getSecondaryPart(stack) : null;
        ItemPartData partRod = item.getRodPart(stack);
        ItemPartData partTip = item.getTipPart(stack);
        ItemPartData partBowstring = item.getBowstringPart(stack);

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

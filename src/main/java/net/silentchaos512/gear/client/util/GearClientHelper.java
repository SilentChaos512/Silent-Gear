package net.silentchaos512.gear.client.util;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.StackHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GearClientHelper {

    public static Map<String, IBakedModel> modelCache = new HashMap<>();

    public static void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {

        I18nHelper i18n = SilentGear.i18n;

        if (stack.getItem() instanceof ICoreItem) {
            boolean ctrlDown = flag instanceof TooltipFlagTC ? ((TooltipFlagTC) flag).ctrlDown : KeyTrackerSL.isControlDown();
            boolean altDown = flag instanceof TooltipFlagTC ? ((TooltipFlagTC) flag).altDown : KeyTrackerSL.isAltDown();

            ICoreItem item = (ICoreItem) stack.getItem();

            if (GearHelper.isBroken(stack)) {
                tooltip.add(1, TextFormatting.RED + i18n.translate("misc", "broken"));
            }

            if (GearData.isExampleGear(stack)) {
                tooltip.add(1, TextFormatting.YELLOW + i18n.translate("misc", "exampleOutput1"));
                tooltip.add(2, TextFormatting.YELLOW + i18n.translate("misc", "exampleOutput2"));
            }

            // Let parts add information if they need to
            for (ItemPartData data : GearData.getConstructionParts(stack)) {
                data.getPart().addInformation(data, stack, world, tooltip, flag.isAdvanced());
            }

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
            String strStats = TextFormatting.GOLD + i18n.translate("misc", "tooltip.stats.name");
            if (ctrlDown) {
                tooltip.add(strStats);
                // Display only stats relevant to the item class
                for (ItemStat stat : item.getRelevantStats(stack)) {
                    float statValue = GearData.getStat(stack, stat);

                    StatInstance inst = new StatInstance("display_" + stat.getUnlocalizedName(), statValue, StatInstance.Operation.AVG);
                    String nameStr = "- " + stat.displayColor + i18n.translate("stat", stat.getUnlocalizedName() + ".name");
                    String statStr = inst.formattedString(stat.displayAsInt ? 0 : 1, false);
                    if (stat == CommonItemStats.DURABILITY) {
                        int durabilityLeft = stack.getMaxDamage() - stack.getItemDamage();
                        int durabilityMax = stack.getMaxDamage();
                        statStr = i18n.translate("stat", "durabilityFormat", durabilityLeft, durabilityMax);
                    }
                    tooltip.add(i18n.translate("stat", "displayFormat", nameStr, statStr));
                }
            } else {
                strStats += " " + TextFormatting.GRAY + i18n.translate("misc", "tooltip.stats.key");
                tooltip.add(strStats);
            }

            // Tool construction
            String strConstruction = TextFormatting.GOLD + i18n.translate("misc", "tooltip.construction.name");
            if (altDown) {
                tooltip.add(strConstruction);
                for (ItemPartData data : GearData.getConstructionParts(stack)) {
                    String str = data.getTranslatedName(stack);
                    if (data.getPart() instanceof PartMain)
                        str += TextFormatting.DARK_GRAY + " (" + data.getGrade().getTranslatedName() + ")";
                    tooltip.add("- " + str);
                }
            } else {
                strConstruction += " " + TextFormatting.GRAY + i18n.translate("misc", "tooltip.construction.key");
                tooltip.add(strConstruction);
            }
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

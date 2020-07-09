package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.RepairContext;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RepairKitItem extends Item {
    private static final String NBT_REPAIR_VALUES = "RepairValues";

    private final int maxValue;

    public RepairKitItem(int maxValue, Properties properties) {
        super(properties);
        this.maxValue = maxValue;
    }

    public boolean addMaterial(ItemStack repairKit, ItemStack materialStack) {
        int totalValue = getTotalRepairValue(repairKit);
        if (totalValue >= this.maxValue) {
            // Repair kit is full
            return false;
        }
        int remainingSpace = this.maxValue - totalValue;

        MaterialInstance mat = MaterialInstance.from(materialStack);
        if (mat != null) {
            int amount = Math.min(remainingSpace, mat.getRepairValue());
            int tier = mat.getMaterial().getTier(PartType.MAIN);
            int current = getStoredRepairValue(repairKit, tier);
            setStoredRepairValue(repairKit, tier, current + amount);
            return amount > 0;
        }

        // Old style parts
        PartData part = PartData.from(materialStack);
        if (part != null && part.getType() == PartType.MAIN) {
            float repairEfficiency = part.computeStat(ItemStats.REPAIR_EFFICIENCY);
            int amount = Math.round(part.computeStat(ItemStats.DURABILITY) * (repairEfficiency > 0 ? repairEfficiency : 1));
            int tier = part.getTier();
            int current = getStoredRepairValue(repairKit, tier);
            setStoredRepairValue(repairKit, tier, amount + current + 1);
            return amount > 0;
        }

        return false;
    }

    private static int getStoredRepairValue(ItemStack stack, int tier) {
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_REPAIR_VALUES);
        return nbt.getInt(String.valueOf(tier));
    }

    private static int getAvailableRepairValue(ItemStack stack, int minTier) {
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_REPAIR_VALUES);
        return nbt.keySet().stream()
                .filter(key -> safeParse(key) >= minTier)
                .mapToInt(nbt::getInt)
                .sum();
    }

    private static int getTotalRepairValue(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_REPAIR_VALUES);
        return nbt.keySet().stream()
                .mapToInt(nbt::getInt)
                .sum();
    }

    private static Collection<Integer> getStoredTiers(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_REPAIR_VALUES);
        return nbt.keySet().stream()
                .map(RepairKitItem::safeParse)
                .filter(i -> i >= 0)
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
    }

    private void setStoredRepairValue(ItemStack stack, int tier, int value) {
        int clamped = MathHelper.clamp(value, 0, this.maxValue);
        String key = String.valueOf(tier);
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_REPAIR_VALUES);

        if (clamped < 0) {
            nbt.remove(key);
        } else {
            nbt.putInt(key, clamped);
        }
    }

    public static int getDamageToRepair(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        int storedValue = getAvailableRepairValue(repairKit, GearData.getTier(gear));
        float gearMulti = GearData.getStat(gear, ItemStats.REPAIR_EFFICIENCY);
        int maxRepair = Math.round(storedValue * gearMulti * repairType.getEfficiency());
        int ret = Math.min(maxRepair, gear.getDamage());
        SilentGear.LOGGER.debug("RepairKitItem#getDamageToRepair: {} * {} * {} = {} -> {}",
                storedValue, gearMulti, repairType.getEfficiency(), maxRepair, ret);
        return ret;
    }

    public void removeRepairMaterial(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType, int damageRepaired) {
        float gearMulti = GearData.getStat(gear, ItemStats.REPAIR_EFFICIENCY);
        int valueUsed = Math.round(damageRepaired / gearMulti / repairType.getEfficiency());

        int gearTier = GearData.getTier(gear);
        for (int tier : getStoredTiers(repairKit)) {
            if (tier >= gearTier) {
                int current = getStoredRepairValue(repairKit, tier);
                setStoredRepairValue(repairKit, tier, current - valueUsed);
                valueUsed -= current - getStoredRepairValue(repairKit, tier);

                if (valueUsed <= 0) {
                    break;
                }
            }

            if (getStoredRepairValue(repairKit, tier) <= 0) {
                repairKit.getOrCreateChildTag(NBT_REPAIR_VALUES).remove(String.valueOf(tier));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "repair_kit.repairValue", format(getTotalRepairValue(stack)), format(this.maxValue)));
        for (int tier : getStoredTiers(stack)) {
            int value = getStoredRepairValue(stack, tier);
            if (value >= 0) {
                tooltip.add(TextUtil.translate("item", "repair_kit.repairValue.tier", tier, format(value)));
            }
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - (float) getTotalRepairValue(stack) / this.maxValue;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3f + 0.5f, 1f, 1f);
    }

    private static int safeParse(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static String format(int k) {
        return String.format("%,d", k);
    }
}

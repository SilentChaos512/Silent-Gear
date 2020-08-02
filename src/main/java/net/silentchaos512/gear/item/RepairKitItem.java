package net.silentchaos512.gear.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.parts.RepairContext;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RepairKitItem extends Item {
    private static final String NBT_STORAGE = "Storage";

    private final Supplier<Integer> capacity;
    private final Supplier<Double> efficiency;

    public RepairKitItem(Supplier<Integer> capacity, Supplier<Double> efficiency, Properties properties) {
        super(properties);
        this.capacity = capacity;
        this.efficiency = efficiency;
    }

    public boolean addMaterial(ItemStack repairKit, ItemStack materialStack) {
        float storedAmount = getStoredMaterialAmount(repairKit);
        if (storedAmount > getKitCapacity() - 1) {
            // Repair kit is full
            return false;
        }

        MaterialInstance mat = MaterialInstance.from(materialStack);
        if (mat != null) {
            String key = getShorthandKey(mat);
            CompoundNBT storageTag = repairKit.getOrCreateChildTag(NBT_STORAGE);
            float current = storageTag.getFloat(key);
            storageTag.putFloat(key, current + 1);
            return true;
        }

        return false;
    }

    private int getKitCapacity() {
        return this.capacity.get();
    }

    private float getRepairEfficiency(RepairContext.Type repairType) {
        return efficiency.get().floatValue() + repairType.getBonusEfficiency();
    }

    private static float getStoredAmount(ItemStack stack, MaterialInstance material) {
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_STORAGE);
        return nbt.getFloat(getShorthandKey(material));
    }

    private static float getStoredMaterialAmount(ItemStack repairKit) {
        return (float) getStoredMaterials(repairKit).values().stream().mapToDouble(f -> f).sum();
    }

    private static Map<MaterialInstance, Float> getStoredMaterials(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateChildTag(NBT_STORAGE);
        List<MaterialInstance> list = nbt.keySet().stream()
                .map(MaterialInstance::readShorthand)
                .filter(Objects::nonNull)
                .sorted(Comparator.<MaterialInstance, Integer>comparing(mat1 -> mat1.getTier(PartType.MAIN))
                        .thenComparing(mat1 -> mat1.getDisplayName(PartType.MAIN).getFormattedText()))
                .collect(Collectors.toList());

        Map<MaterialInstance, Float> ret = new LinkedHashMap<>();
        list.forEach(mat -> {
            float value = nbt.getFloat(getShorthandKey(mat));
            ret.put(mat, value);
        });
        return ret;
    }

    @Nonnull
    private static String getShorthandKey(MaterialInstance mat) {
        return MaterialInstance.writeShorthand(mat);
    }

    private Pair<Map<MaterialInstance, Float>, Integer> getMaterialsToRepair(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        // Materials should be sorted by tier (ascending)
        Map<MaterialInstance, Float> stored = getStoredMaterials(repairKit);
        Map<MaterialInstance, Float> used = new HashMap<>();
        float gearRepairEfficiency = GearData.getStat(gear, ItemStats.REPAIR_EFFICIENCY);
        float kitEfficiency = this.getRepairEfficiency(repairType);
        int damageLeft = gear.getDamage();

        if (gearRepairEfficiency > 0f && kitEfficiency > 0f) {
            for (Map.Entry<MaterialInstance, Float> entry : stored.entrySet()) {
                MaterialInstance mat = entry.getKey();
                float amount = entry.getValue();

                int repairValue = mat.getRepairValue(gear);
                if (repairValue > 0) {
                    float totalRepairValue = repairValue * amount;
                    int maxRepair = Math.round(totalRepairValue * gearRepairEfficiency * kitEfficiency);
                    int toRepair = Math.min(maxRepair, damageLeft);
                    damageLeft -= toRepair;
                    float repairValueUsed = toRepair / gearRepairEfficiency / kitEfficiency;
                    float amountUsed = repairValueUsed / repairValue;
                    used.put(mat, amountUsed);

                    if (damageLeft <= 0) {
                        break;
                    }
                }
            }
        }

        return Pair.of(used, gear.getDamage() - damageLeft);
    }

    public Map<MaterialInstance, Float> getRepairMaterials(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        return getMaterialsToRepair(gear, repairKit, repairType).getFirst();
    }

    public int getDamageToRepair(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        return getMaterialsToRepair(gear, repairKit, repairType).getSecond();
    }

    public void removeRepairMaterials(ItemStack repairKit, Map<MaterialInstance, Float> toRemove) {
        CompoundNBT nbt = repairKit.getOrCreateChildTag(NBT_STORAGE);
        for (Map.Entry<MaterialInstance, Float> entry : toRemove.entrySet()) {
            MaterialInstance mat = entry.getKey();
            Float amount = entry.getValue();

            String key = getShorthandKey(mat);
            float newValue = nbt.getFloat(key) - amount;

            if (newValue < 0.01f) {
                nbt.remove(key);
            } else {
                nbt.putFloat(key, newValue);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "repair_kit.efficiency",
                (int) (this.getRepairEfficiency(RepairContext.Type.QUICK) * 100)));
        tooltip.add(TextUtil.translate("item", "repair_kit.capacity",
                format(getStoredMaterialAmount(stack)),
                getKitCapacity()));

        Map<MaterialInstance, Float> storedMaterials = getStoredMaterials(stack);
        if (storedMaterials.isEmpty()) {
            tooltip.add(TextUtil.translate("item", "repair_kit.hint1").applyTextStyle(TextFormatting.ITALIC));
            tooltip.add(TextUtil.translate("item", "repair_kit.hint2").applyTextStyle(TextFormatting.ITALIC));
            tooltip.add(TextUtil.translate("item", "repair_kit.hint3").applyTextStyle(TextFormatting.ITALIC));
            return;
        }

        for (Map.Entry<MaterialInstance, Float> entry : storedMaterials.entrySet()) {
            tooltip.add(TextUtil.translate("item", "repair_kit.material",
                    entry.getKey().getDisplayNameWithGrade(PartType.MAIN),
                    format(entry.getValue())));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - getStoredMaterialAmount(stack) / getKitCapacity();
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3f + 0.5f, 1f, 1f);
    }

    private static String format(float f) {
        return String.format("%.2f", f);
    }
}

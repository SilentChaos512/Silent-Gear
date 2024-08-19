package net.silentchaos512.gear.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.RepairContext;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RepairKitItem extends Item {
    private final Supplier<Integer> capacity;
    private final Supplier<Double> efficiency;

    public RepairKitItem(Supplier<Integer> capacity, Supplier<Double> efficiency, Properties properties) {
        super(properties);
        this.capacity = capacity;
        this.efficiency = efficiency;
    }

    public boolean addMaterial(ItemStack repairKit, ItemStack materialStack) {
        var material = MaterialInstance.from(materialStack);
        if (material == null) return false;

        var materialValue = getMaterialValue(material);

        if (getTotalStoredMaterialAmount(repairKit) > getKitCapacity() - materialValue) {
            // Repair kit is full
            return false;
        }

        var materialStorage = new HashMap<>(repairKit.getOrDefault(SgDataComponents.MATERIAL_STORAGE, Collections.emptyMap()));
        materialStorage.merge(material, materialValue, Float::sum);
        repairKit.set(SgDataComponents.MATERIAL_STORAGE, ImmutableMap.copyOf(materialStorage));

        return true;
    }

    private float getMaterialValue(MaterialInstance material) {
        // TODO: Consider modifiers like grades? Maybe base this on the ratio between modified
        //  and unmodified durability?
        return 1f;
    }

    private int getKitCapacity() {
        return this.capacity.get();
    }

    public float getRepairEfficiency(RepairContext.Type repairType) {
        return efficiency.get().floatValue() + repairType.getBonusEfficiency();
    }

    private static float getStoredAmount(ItemStack stack, MaterialInstance material) {
        var materialStorage = stack.get(SgDataComponents.MATERIAL_STORAGE);
        return materialStorage != null ? materialStorage.getOrDefault(material, 0f) : 0f;
    }

    private static float getTotalStoredMaterialAmount(ItemStack repairKit) {
        float sum = 0f;
        for (float amount : getStoredMaterials(repairKit).values()) {
            sum += amount;
        }
        return sum;
    }

    private static Map<MaterialInstance, Float> getStoredMaterials(ItemStack stack) {
        return new HashMap<>(stack.getOrDefault(SgDataComponents.MATERIAL_STORAGE, Collections.emptyMap()));
    }

    private Pair<Map<MaterialInstance, Float>, Integer> getMaterialsToRepair(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        // Materials should be sorted by tier (ascending)
        Map<MaterialInstance, Float> stored = getStoredMaterials(repairKit);
        Map<MaterialInstance, Float> used = new HashMap<>();
        float gearRepairEfficiency = GearData.getProperties(gear).getNumber(GearProperties.REPAIR_EFFICIENCY);
        float kitEfficiency = this.getRepairEfficiency(repairType);
        int damageLeft = gear.getDamageValue();

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

        return Pair.of(used, gear.getDamageValue() - damageLeft);
    }

    public Map<MaterialInstance, Float> getRepairMaterials(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        return getMaterialsToRepair(gear, repairKit, repairType).getFirst();
    }

    public int getDamageToRepair(ItemStack gear, ItemStack repairKit, RepairContext.Type repairType) {
        return getMaterialsToRepair(gear, repairKit, repairType).getSecond();
    }

    public void removeRepairMaterials(ItemStack repairKit, Map<MaterialInstance, Float> toRemove) {
        var storedMaterials = getStoredMaterials(repairKit);
        toRemove.forEach((material, value) -> {
            if (storedMaterials.containsKey(material)) {
                var newValue = storedMaterials.get(material) - value;
                if (newValue < 0.01f) {
                    storedMaterials.remove(material);
                } else {
                    storedMaterials.put(material, newValue);
                }
            }
        });
        repairKit.set(SgDataComponents.MATERIAL_STORAGE, storedMaterials);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "repair_kit.efficiency",
                (int) (this.getRepairEfficiency(RepairContext.Type.QUICK) * 100)));
        tooltip.add(TextUtil.translate("item", "repair_kit.capacity",
                format(getTotalStoredMaterialAmount(stack)),
                getKitCapacity()));

        Map<MaterialInstance, Float> storedMaterials = getStoredMaterials(stack);
        if (storedMaterials.isEmpty()) {
            tooltip.add(TextUtil.translate("item", "repair_kit.hint1").withStyle(ChatFormatting.ITALIC));
            tooltip.add(TextUtil.translate("item", "repair_kit.hint2").withStyle(ChatFormatting.ITALIC));
            tooltip.add(TextUtil.translate("item", "repair_kit.hint3").withStyle(ChatFormatting.ITALIC));
            return;
        }

        for (Map.Entry<MaterialInstance, Float> entry : storedMaterials.entrySet()) {
            tooltip.add(TextUtil.translate("item", "repair_kit.material",
                    entry.getKey().getDisplayNameWithModifiers(PartTypes.MAIN.get(), ItemStack.EMPTY),
                    format(entry.getValue())));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * getTotalStoredMaterialAmount(stack) / getKitCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0f, (13f - getBarWidth(stack)) / 13f) / 3f + 0.5f, 1f, 1f);
    }

    private static String format(float f) {
        return String.format("%.2f", f);
    }
}

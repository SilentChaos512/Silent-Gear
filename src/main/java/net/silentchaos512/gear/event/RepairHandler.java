package net.silentchaos512.gear.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.part.RepairContext;
import net.silentchaos512.gear.gear.part.UpgradeGearPart;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;

@EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class RepairHandler {
    private RepairHandler() {}

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() instanceof ICoreItem) {
            MaterialInstance material = MaterialInstance.from(event.getRight());
            PartInstance part = PartInstance.from(event.getRight());

            if (material != null) {
                handleGearRepair(event, material);
            } else if (part != null && part.get() instanceof UpgradeGearPart) {
                handleUpgradeApplication(event, part);
            }
        }
    }

    private static void handleUpgradeApplication(AnvilUpdateEvent event, PartInstance part) {
        ItemStack result = event.getLeft().copy();
        applyName(event, result);

        GearData.addUpgradePart(result, part);
        GearData.recalculateGearData(result, null);

        event.setOutput(result);
        // TODO: Upgrade cost?
        event.setCost(3);
    }

    private static void handleGearRepair(AnvilUpdateEvent event, MaterialInstance material) {
        ItemStack result = event.getLeft().copy();
        applyName(event, result);

        float repairValue = material.getRepairValue(result, RepairContext.Type.ANVIL);
        float gearRepairEfficiency = GearData.getProperties(result, event.getPlayer()).getNumber(GearProperties.REPAIR_EFFICIENCY);
        float anvilEfficiency = Config.Common.repairFactorAnvil.get().floatValue();
        float amount = repairValue * gearRepairEfficiency * anvilEfficiency;

        // How many of materials to use?
        int materialCount = 1;
        float repaired = amount;
        while (materialCount < event.getRight().getCount() && repaired < result.getDamageValue()) {
            ++materialCount;
            repaired += amount;
        }

        if (amount > 0) {
            var repairAmount = Math.round(amount * materialCount);
            result.setDamageValue(result.getDamageValue() - repairAmount);
            GearData.recalculateGearData(result, null);
            event.setOutput(result);
            event.setCost(materialCount);
            event.setMaterialCost(materialCount);
        }
    }

    private static void applyName(AnvilUpdateEvent event, ItemStack stack) {
        if (event.getName() != null && !event.getName().isEmpty()) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(event.getName()));
        }
    }
}

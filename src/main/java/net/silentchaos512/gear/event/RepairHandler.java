package net.silentchaos512.gear.event;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.RepairContext;
import net.silentchaos512.gear.parts.type.PartMain;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.advancements.LibTriggers;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class RepairHandler {
    private static final ResourceLocation APPLY_TIP_UPGRADE = SilentGear.getId("apply_tip_upgrade");
    private static final ResourceLocation MAX_DURABILITY = SilentGear.getId("max_durability");
    private static final ResourceLocation REPAIR_FROM_BROKEN = SilentGear.getId("repair_from_broken");

    private RepairHandler() {}

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() instanceof ICoreItem) {
            PartData part = PartData.from(event.getRight());

            if (part != null) {
                if (part.getPart() instanceof IUpgradePart) {
                    handleUpgradeApplication(event, part);
                } else if (part.getPart() instanceof PartMain) {
                    handleGearRepair(event, part);
                }
            }
        }
    }

    private static void handleUpgradeApplication(AnvilUpdateEvent event, PartData part) {
        ItemStack result = event.getLeft().copy();
        GearData.addUpgradePart(result, part);
        GearData.recalculateStats(result, null);

        event.setOutput(result);
        // TODO: Upgrade cost?
        event.setCost(3);
    }

    private static void handleGearRepair(AnvilUpdateEvent event, PartData part) {
        ItemStack result = event.getLeft().copy();
        float amount = part.getRepairAmount(result, RepairContext.Type.ANVIL);
        amount *= GearData.getStat(result, CommonItemStats.REPAIR_EFFICIENCY);

        // How many of materials to use?
        int materialCount = 1;
        float repaired = amount;
        while (materialCount < event.getRight().getCount() && repaired < result.getDamage()) {
            ++materialCount;
            repaired += amount;
        }

        if (amount > 0) {
            result.attemptDamageItem(-Math.round(amount * materialCount), SilentGear.random, null);
            GearData.recalculateStats(result, null);
            event.setOutput(result);
            event.setCost(materialCount);
            event.setMaterialCost(materialCount);
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!(result.getItem() instanceof ICoreItem)) return;

        if (event.getPlayer() instanceof ServerPlayerEntity) {
            // Try to trigger some advancments
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

            // Repair from broken
            int brokenCount = GearData.getBrokenCount(result);
            int repairCount = GearData.getRepairCount(result);
            if (brokenCount > 0 && repairCount > 0) {
                LibTriggers.GENERIC_INT.trigger(player, REPAIR_FROM_BROKEN, brokenCount);
            }

            // High durability
            LibTriggers.GENERIC_INT.trigger(player, MAX_DURABILITY, result.getMaxDamage());

            // Add tip upgrade
            if (!GearData.getConstructionParts(result).getTips().isEmpty()) {
                LibTriggers.GENERIC_INT.trigger(player, APPLY_TIP_UPGRADE, 1);
            }
        }
    }
}

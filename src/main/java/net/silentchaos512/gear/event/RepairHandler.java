package net.silentchaos512.gear.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.parts.type.PartMain;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.RepairContext;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.advancements.LibTriggers;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class RepairHandler {
    private static final ResourceLocation APPLY_TIP_UPGRADE = new ResourceLocation(SilentGear.MOD_ID, "apply_tip_upgrade");
    private static final ResourceLocation MAX_DURABILITY = new ResourceLocation(SilentGear.MOD_ID, "max_durability");
    private static final ResourceLocation REPAIR_FROM_BROKEN = new ResourceLocation(SilentGear.MOD_ID, "repair_from_broken");

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
        GearData.recalculateStats(null, result);

        event.setOutput(result);
        // TODO: Upgrade cost?
        event.setCost(3);
    }

    private static void handleGearRepair(AnvilUpdateEvent event, PartData part) {
        ItemStack result = event.getLeft().copy();
        // TODO: Need to consider stack size!
        float amount =  part.getRepairAmount(result, RepairContext.Type.ANVIL);
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
            GearData.recalculateStats(result);
            event.setOutput(result);
            // TODO: Repair cost?
            event.setCost(materialCount);
            event.setMaterialCost(materialCount);
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.crafting.getItem() instanceof ICoreItem)) return;

        if (event.player instanceof EntityPlayerMP) {
            // Try to trigger some advancments
            EntityPlayerMP player = (EntityPlayerMP) event.player;

            // Repair from broken
            int brokenCount = GearData.getBrokenCount(event.crafting);
            int repairCount = GearData.getRepairCount(event.crafting);
            if (brokenCount > 0 && repairCount > 0) {
                LibTriggers.GENERIC_INT.trigger(player, REPAIR_FROM_BROKEN, brokenCount);
            }

            // High durability
            LibTriggers.GENERIC_INT.trigger(player, MAX_DURABILITY, event.crafting.getMaxDamage());

            // Add tip upgrade
            if (!GearData.getConstructionParts(event.crafting).getTips().isEmpty())
                LibTriggers.GENERIC_INT.trigger(player, APPLY_TIP_UPGRADE, 1);
        }
    }
}

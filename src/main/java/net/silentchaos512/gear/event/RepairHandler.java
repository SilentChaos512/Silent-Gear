package net.silentchaos512.gear.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.advancements.LibTriggers;

public class RepairHandler {
    public static final RepairHandler INSTANCE = new RepairHandler();

    private static final ResourceLocation APPLY_TIP_UPGRADE = new ResourceLocation(SilentGear.MOD_ID, "apply_tip_upgrade");
    private static final ResourceLocation MAX_DURABILITY = new ResourceLocation(SilentGear.MOD_ID, "max_durability");
    private static final ResourceLocation REPAIR_FROM_BROKEN = new ResourceLocation(SilentGear.MOD_ID, "repair_from_broken");

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        // TODO
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
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
            if (GearData.getConstructionParts(event.crafting).getTips().size() > 0)
                LibTriggers.GENERIC_INT.trigger(player, APPLY_TIP_UPGRADE, 1);
        }
    }
}

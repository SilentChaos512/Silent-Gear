package net.silentchaos512.gear.event;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RepairHandler {

    public static final RepairHandler INSTANCE = new RepairHandler();

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        // TODO
    }
}

package net.silentchaos512.gear.event;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldHandler {

    public static final WorldHandler INSTANCE = new WorldHandler();

    private WorldHandler() {
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().getMinecraftServer() != null) {
            event.getWorld().addEventListener(new WorldListener(event.getWorld(), event.getWorld().getMinecraftServer()));
        }
    }
}

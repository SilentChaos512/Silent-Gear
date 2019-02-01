package net.silentchaos512.gear.event;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class WorldHandler {
    private WorldHandler() { }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        /* FIXME
        if (event.getWorld().getMinecraftServer() != null) {
            event.getWorld().addEventListener(new WorldListener(event.getWorld(), event.getWorld().getMinecraftServer()));
        }
        */
    }
}

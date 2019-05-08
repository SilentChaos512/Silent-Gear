package net.silentchaos512.gear.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.SyncGearPartsPacket;
import net.silentchaos512.gear.network.SyncTraitsPacket;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitManager;

import java.util.Collection;

/**
 * Events for the server, such as sending data to clients. These events will also fire on single
 * player (even though that is not necessary), as restricting them to dedicated server side only
 * would prevent this from working on LAN games.
 * <p>
 * This was called DedicatedServerEvents in version 1.0.9, and was only registered on dedicated
 * servers, as the name implied.
 */
@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
        if (!(player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) player;

        // FIXME: These are sent too late!
        sendTraitsToClient(playerMP);
        sendPartsToClient(playerMP);
    }

    private static void sendTraitsToClient(EntityPlayerMP playerMP) {
        Collection<ITrait> traits = TraitManager.getValues();
        SilentGear.LOGGER.info("Sending {} traits to {}", traits.size(), playerMP.getScoreboardName());
        SyncTraitsPacket msg = new SyncTraitsPacket(traits);
        Network.channel.sendTo(msg, playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static void sendPartsToClient(EntityPlayerMP playerMP) {
        Collection<IGearPart> parts = PartManager.getValues();
        SilentGear.LOGGER.info("Sending {} gear parts to {}", parts.size(), playerMP.getScoreboardName());
        SyncGearPartsPacket msg = new SyncGearPartsPacket(parts);
        Network.channel.sendTo(msg, playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}

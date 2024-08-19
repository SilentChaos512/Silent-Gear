package net.silentchaos512.gear.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.network.payload.server.SyncMaterialsPayload;
import net.silentchaos512.gear.network.payload.server.SyncPartsPayload;
import net.silentchaos512.gear.network.payload.server.SyncTraitsPayload;
import net.silentchaos512.gear.setup.SgRegistries;

@EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            ServerPlayer player = event.getPlayer();
            PacketDistributor.sendToPlayer(
                    player,
                    new SyncTraitsPayload(),
                    new SyncMaterialsPayload(),
                    new SyncPartsPayload()
            );
        } else {
            PacketDistributor.sendToAllPlayers(
                    new SyncTraitsPayload(),
                    new SyncMaterialsPayload(),
                    new SyncPartsPayload()
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        SgRegistries.TRAIT.getErrorMessages(serverPlayer).forEach(serverPlayer::sendSystemMessage);
        SgRegistries.MATERIAL.getErrorMessages(serverPlayer).forEach(serverPlayer::sendSystemMessage);
        SgRegistries.PART.getErrorMessages(serverPlayer).forEach(serverPlayer::sendSystemMessage);
    }
}

package net.silentchaos512.gear.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.network.SgNetwork;
import net.silentchaos512.gear.network.SyncGearCraftingItemsPacket;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;

/**
 * Handles chat error messages. Previously handled sending parts and traits to the client, but this
 * is done with login messages now that that is possible.
 */
@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) return;

        ServerPlayer playerMP = (ServerPlayer) player;

        // Send crafting items packets to correct for registry changes
        SilentGear.LOGGER.debug("Sending materials crafting item correction packet");
        SgNetwork.channel.sendTo(new SyncMaterialCraftingItemsPacket(MaterialManager.getValues()), playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        SilentGear.LOGGER.debug("Sending parts crafting item correction packet");
        SgNetwork.channel.sendTo(new SyncGearCraftingItemsPacket(), playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

        TraitManager.getErrorMessages(playerMP).forEach(playerMP::sendSystemMessage);
        MaterialManager.getErrorMessages(playerMP).forEach(playerMP::sendSystemMessage);
        PartManager.getErrorMessages(playerMP).forEach(playerMP::sendSystemMessage);
    }
}

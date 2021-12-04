package net.silentchaos512.gear.event;

import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.network.Network;
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
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer)) return;

        ServerPlayer playerMP = (ServerPlayer) player;

        // Send crafting items packets to correct for registry changes
        SilentGear.LOGGER.debug("Sending materials crafting item correction packet");
        Network.channel.sendTo(new SyncMaterialCraftingItemsPacket(MaterialManager.getValues()), playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        SilentGear.LOGGER.debug("Sending parts crafting item correction packet");
        Network.channel.sendTo(new SyncGearCraftingItemsPacket(), playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

        TraitManager.getErrorMessages(playerMP).forEach(text -> playerMP.sendMessage(text, Util.NIL_UUID));
        MaterialManager.getErrorMessages(playerMP).forEach(text -> playerMP.sendMessage(text, Util.NIL_UUID));
        PartManager.getErrorMessages(playerMP).forEach(text -> playerMP.sendMessage(text, Util.NIL_UUID));
    }
}

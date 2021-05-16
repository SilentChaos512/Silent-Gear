package net.silentchaos512.gear.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.SyncGearCraftingItemsPacket;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;

/**
 * Handles chat error messages. Previously handled sending parts and traits to the client, but this
 * is done with login messages now that that is possible.
 */
@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!(player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity playerMP = (ServerPlayerEntity) player;

        // Send crafting items packets to correct for registry changes
        SilentGear.LOGGER.debug("Sending materials crafting item correction packet");
        Network.channel.sendTo(new SyncMaterialCraftingItemsPacket(MaterialManager.getValues()), playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        SilentGear.LOGGER.debug("Sending parts crafting item correction packet");
        Network.channel.sendTo(new SyncGearCraftingItemsPacket(), playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);

        TraitManager.getErrorMessages(playerMP).forEach(text -> playerMP.sendMessage(text, Util.DUMMY_UUID));
        MaterialManager.getErrorMessages(playerMP).forEach(text -> playerMP.sendMessage(text, Util.DUMMY_UUID));
        PartManager.getErrorMessages(playerMP).forEach(text -> playerMP.sendMessage(text, Util.DUMMY_UUID));
    }
}

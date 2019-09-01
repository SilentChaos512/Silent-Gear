package net.silentchaos512.gear.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitManager;

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

        PartManager.getErrorMessages(playerMP).forEach(playerMP::sendMessage);
        TraitManager.getErrorMessages(playerMP).forEach(playerMP::sendMessage);
    }
}

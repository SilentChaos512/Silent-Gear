package net.silentchaos512.gear.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.material.GearDisplayManager;
import net.silentchaos512.gear.network.GearLeftClickPacket;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.util.GearHelper;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SilentGear.MOD_ID)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            HitResult raytrace = mc.hitResult;

            if (player != null && (raytrace == null || raytrace.getType() == HitResult.Type.MISS)) {
                ItemStack stack = player.getMainHandItem();

                if (GearHelper.isGear(stack)) {
                    Network.channel.sendToServer(new GearLeftClickPacket());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        GearDisplayManager.getErrorMessages(player).forEach(player::sendSystemMessage);
    }
}

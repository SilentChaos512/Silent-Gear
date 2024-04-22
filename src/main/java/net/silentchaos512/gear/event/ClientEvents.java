package net.silentchaos512.gear.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.material.GearDisplayManager;
import net.silentchaos512.gear.network.GearLeftClickPacket;
import net.silentchaos512.gear.network.SgNetwork;
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
                    SgNetwork.channel.sendToServer(new GearLeftClickPacket());
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

package net.silentchaos512.gear.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.network.GearLeftClickPacket;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.util.GearHelper;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SilentGear.MOD_ID)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onClick(InputEvent.ClickInputEvent event) {
        if (event.isAttack()) {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;
            RayTraceResult raytrace = mc.objectMouseOver;

            if (player != null && (raytrace == null || raytrace.getType() == RayTraceResult.Type.MISS)) {
                ItemStack stack = player.getHeldItemMainhand();

                if (GearHelper.isGear(stack)) {
                    Network.channel.sendToServer(new GearLeftClickPacket());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        MaterialDisplayManager.getErrorMessages(player).forEach(text -> player.sendMessage(text, Util.DUMMY_UUID));
    }
}

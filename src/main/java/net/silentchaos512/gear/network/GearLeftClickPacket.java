package net.silentchaos512.gear.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class GearLeftClickPacket {
    public GearLeftClickPacket() {
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = context.get().getSender();
        if (player != null) {
            ItemStack stack = player.getHeldItemMainhand();
            if (GearHelper.isGear(stack)) {
                GearHelper.onItemSwing(stack, player);
            }
        }
    }

    public static GearLeftClickPacket decode(PacketBuffer buffer) {
        return new GearLeftClickPacket();
    }

    public void encode(PacketBuffer buffer) {
    }
}

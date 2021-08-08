package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class GearLeftClickPacket {
    public GearLeftClickPacket() {
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (GearHelper.isGear(stack)) {
                GearHelper.onItemSwing(stack, player);
            }
        }
    }

    public static GearLeftClickPacket decode(FriendlyByteBuf buffer) {
        return new GearLeftClickPacket();
    }

    public void encode(FriendlyByteBuf buffer) {
    }
}

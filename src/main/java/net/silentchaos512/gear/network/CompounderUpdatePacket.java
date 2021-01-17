package net.silentchaos512.gear.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.block.compounder.CompounderContainer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CompounderUpdatePacket {
    private final boolean workEnabled;

    public CompounderUpdatePacket(boolean workEnabled) {
        this.workEnabled = workEnabled;
    }

    public static CompounderUpdatePacket decode(PacketBuffer buffer) {
        boolean workEnabled = buffer.readBoolean();
        return new CompounderUpdatePacket(workEnabled);
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(this.workEnabled);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = context.get().getSender();
        context.get().enqueueWork(() -> handlePacket(player));
        context.get().setPacketHandled(true);
    }

    private void handlePacket(@Nullable ServerPlayerEntity player) {
        if (player != null && player.openContainer instanceof CompounderContainer) {
            CompounderContainer container = (CompounderContainer) player.openContainer;
            container.setWorkEnabled(this.workEnabled);
        }
    }
}

package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.gear.block.compounder.CompounderContainer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CompounderUpdatePacket {
    private final boolean workEnabled;

    public CompounderUpdatePacket(boolean workEnabled) {
        this.workEnabled = workEnabled;
    }

    public static CompounderUpdatePacket decode(FriendlyByteBuf buffer) {
        boolean workEnabled = buffer.readBoolean();
        return new CompounderUpdatePacket(workEnabled);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.workEnabled);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        context.get().enqueueWork(() -> handlePacket(player));
        context.get().setPacketHandled(true);
    }

    private void handlePacket(@Nullable ServerPlayer player) {
        if (player != null && player.containerMenu instanceof CompounderContainer) {
            CompounderContainer container = (CompounderContainer) player.containerMenu;
            container.setWorkEnabled(this.workEnabled);
        }
    }
}

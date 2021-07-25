package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.command.MaterialsCommand;
import net.silentchaos512.gear.command.TraitsCommand;

import java.util.function.Supplier;

public class ClientOutputCommandPacket {
    public enum Type {
        MATERIALS, TRAITS
    }

    private final Type type;
    private final boolean includeChildren;

    public ClientOutputCommandPacket(Type type, boolean includeChildren) {
        this.type = type;
        this.includeChildren = includeChildren;
    }

    public static ClientOutputCommandPacket decode(FriendlyByteBuf buffer) {
        Type type = buffer.readEnum(Type.class);
        boolean includeChildren = buffer.readBoolean();
        return new ClientOutputCommandPacket(type, includeChildren);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.type);
        buffer.writeBoolean(this.includeChildren);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        switch (this.type) {
            case MATERIALS:
                MaterialsCommand.runDumpClient(this.includeChildren);
                break;
            case TRAITS:
                TraitsCommand.runDumpMdClient();
                break;
            default:
                SilentGear.LOGGER.error("Unknown ClientOutputCommandPacket.Type: {}", this.type);
        }

        context.get().setPacketHandled(true);
    }
}

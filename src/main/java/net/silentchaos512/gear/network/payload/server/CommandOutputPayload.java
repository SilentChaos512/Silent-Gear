package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

public record CommandOutputPayload(
        CommandType commandType,
        boolean includeChildren
) implements CustomPacketPayload {
    public static final Type<CommandOutputPayload> TYPE = new Type<>(SilentGear.getId("command_output"));

    public static final StreamCodec<FriendlyByteBuf, CommandOutputPayload> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeBoolean(data.commandType == CommandType.MATERIALS);
                buf.writeBoolean(data.includeChildren);
            },
            buf -> {
                var commandType = buf.readBoolean() ? CommandType.MATERIALS : CommandType.TRAITS;
                var includeChildren = buf.readBoolean();
                return new CommandOutputPayload(commandType, includeChildren);
            }
    );

    public static CommandOutputPayload materials(boolean includeChildren) {
        return new CommandOutputPayload(CommandType.MATERIALS, includeChildren);
    }

    public static CommandOutputPayload traits() {
        return new CommandOutputPayload(CommandType.TRAITS, true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum CommandType {
        MATERIALS, TRAITS;
    }
}

package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public record CommandOutputPayload(Type type, boolean includeChildren) implements CustomPacketPayload {
    public enum Type {
        MATERIALS, TRAITS;
    }

    public static final ResourceLocation ID = SilentGear.getId("command_client_output");

    public static CommandOutputPayload materials(boolean includeChildren) {
        return new CommandOutputPayload(Type.MATERIALS, includeChildren);
    }

    public static CommandOutputPayload traits() {
        return new CommandOutputPayload(Type.TRAITS, true);
    }

    public CommandOutputPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean() ? Type.MATERIALS : Type.TRAITS, buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(type == Type.MATERIALS);
        friendlyByteBuf.writeBoolean(includeChildren);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

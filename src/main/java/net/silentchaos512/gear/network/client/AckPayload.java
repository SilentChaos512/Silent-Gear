package net.silentchaos512.gear.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.silentchaos512.gear.SilentGear;

public record AckPayload(ConfigurationTask.Type type) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("ack");

    public AckPayload(FriendlyByteBuf buf) {
        this(new ConfigurationTask.Type(buf.readUtf()));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(this.type.id());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

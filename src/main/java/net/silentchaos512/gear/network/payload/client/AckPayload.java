package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

public record AckPayload() implements CustomPacketPayload {
    public static final Type<AckPayload> TYPE = new Type<>(SilentGear.getId("ack"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

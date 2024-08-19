package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

public record SwingGearPayload() implements CustomPacketPayload {
    public static final Type<SwingGearPayload> TYPE = new Type<>(SilentGear.getId("swing_gear"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

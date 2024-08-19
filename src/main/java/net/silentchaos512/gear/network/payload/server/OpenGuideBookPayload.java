package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

public record OpenGuideBookPayload() implements CustomPacketPayload {
    public static final Type<OpenGuideBookPayload> TYPE = new Type<>(SilentGear.getId("open_guide_book"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

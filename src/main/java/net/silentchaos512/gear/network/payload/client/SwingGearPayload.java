package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public record SwingGearPayload() implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("swing_gear");

    public SwingGearPayload(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        // No data to write
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

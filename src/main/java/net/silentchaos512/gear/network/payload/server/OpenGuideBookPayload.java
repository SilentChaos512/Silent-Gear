package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public record OpenGuideBookPayload() implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("open_guide_book");

    public OpenGuideBookPayload(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        // No data to write
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public record AlloyMakerUpdatePayload(boolean workEnabled) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("alloy_maker_update");

    public AlloyMakerUpdatePayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(workEnabled);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

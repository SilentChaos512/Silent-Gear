package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public record SelectBlueprintInBookPayload(int bookSlot, int blueprintSlot) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("select_blueprint_in_book");

    public SelectBlueprintInBookPayload(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(bookSlot);
        pBuffer.writeVarInt(blueprintSlot);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

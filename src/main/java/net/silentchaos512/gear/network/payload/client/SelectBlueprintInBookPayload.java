package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

public record SelectBlueprintInBookPayload(int bookSlot, int blueprintSlot) implements CustomPacketPayload {
    public static final Type<SelectBlueprintInBookPayload> TYPE = new Type<>(SilentGear.getId("select_blueprint_in_book"));

    public static final StreamCodec<FriendlyByteBuf, SelectBlueprintInBookPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, d -> d.bookSlot,
            ByteBufCodecs.VAR_INT, d -> d.blueprintSlot,
            SelectBlueprintInBookPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

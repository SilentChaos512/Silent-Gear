package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

public record AlloyMakerUpdatePayload(boolean workEnabled) implements CustomPacketPayload {
    public static final Type<AlloyMakerUpdatePayload> TYPE = new Type<>(SilentGear.getId("alloy_maker_update"));

    public static final StreamCodec<FriendlyByteBuf, AlloyMakerUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, d -> d.workEnabled,
            AlloyMakerUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

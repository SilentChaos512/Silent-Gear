package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;

// Formerly AlloyMakerUpdatePayload
public record ToggleWorkModePayload(boolean workEnabled) implements CustomPacketPayload {
    public static final Type<ToggleWorkModePayload> TYPE = new Type<>(SilentGear.getId("toggle_work_mode"));

    public static final StreamCodec<FriendlyByteBuf, ToggleWorkModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, d -> d.workEnabled,
            ToggleWorkModePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

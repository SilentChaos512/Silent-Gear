package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public record RecalculateStatsPayload(int slot, Supplier<GearProperty<?, ?>> triggerStat) implements CustomPacketPayload {
    public static final Type<RecalculateStatsPayload> TYPE = new Type<>(SilentGear.getId("recalculate_stats"));

    public static final StreamCodec<FriendlyByteBuf, RecalculateStatsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeVarInt(data.slot);
                buf.writeResourceLocation(Objects.requireNonNull(SgRegistries.GEAR_PROPERTY.getKey(data.triggerStat.get())));
            },
            buf -> {
                var slot = buf.readVarInt();
                var id = buf.readResourceLocation();
                return new RecalculateStatsPayload(slot, () -> SgRegistries.GEAR_PROPERTY.get(id));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

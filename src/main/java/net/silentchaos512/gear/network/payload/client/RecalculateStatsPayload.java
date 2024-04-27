package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;

import java.util.Objects;

public record RecalculateStatsPayload(int slot, IItemStat triggerStat) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("recalculate_stats");

    public RecalculateStatsPayload(FriendlyByteBuf buf) {
        this(buf.readVarInt(), Objects.requireNonNull(ItemStats.byName(buf.readResourceLocation())));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(slot);
        pBuffer.writeResourceLocation(triggerStat.getStatId());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

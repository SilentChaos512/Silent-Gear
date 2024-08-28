package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.HashMap;
import java.util.Map;

public record SyncPartsPayload(Map<ResourceLocation, GearPart> parts) implements CustomPacketPayload, DataResourcesPayload<GearPart> {
    public static final Type<SyncPartsPayload> TYPE = new Type<>(SilentGear.getId("sync_parts"));

    private static final StreamCodec<RegistryFriendlyByteBuf, HashMap<ResourceLocation, GearPart>> MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            ResourceLocation.STREAM_CODEC,
            PartSerializers.DISPATCH_STREAM_CODEC
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPartsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> MAP_STREAM_CODEC.encode(buf, new HashMap<>(data.parts)),
            buf -> new SyncPartsPayload(MAP_STREAM_CODEC.decode(buf))
    );

    public SyncPartsPayload() {
        this(SgRegistries.PART.copyOfMap());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public Map<ResourceLocation, GearPart> values() {
        return parts;
    }
}

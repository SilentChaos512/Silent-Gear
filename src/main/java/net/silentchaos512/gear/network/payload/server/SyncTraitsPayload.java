package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.HashMap;
import java.util.Map;

public record SyncTraitsPayload(Map<Identifier, Trait> traits) implements CustomPacketPayload, DataResourcesPayload<Trait> {
    public static final Type<SyncTraitsPayload> TYPE = new Type<>(SilentGear.getId("sync_traits"));

    private static final StreamCodec<RegistryFriendlyByteBuf, HashMap<Identifier, Trait>> MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            Identifier.STREAM_CODEC,
            Trait.STREAM_CODEC
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTraitsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> MAP_STREAM_CODEC.encode(buf, new HashMap<>(data.traits)),
            buf -> new SyncTraitsPayload(MAP_STREAM_CODEC.decode(buf))
    );

    public SyncTraitsPayload() {
        this(SgRegistries.TRAIT.copyOfMap());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public Map<Identifier, Trait> values() {
        return traits;
    }
}

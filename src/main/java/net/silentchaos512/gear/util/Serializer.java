package net.silentchaos512.gear.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Serializer<V> {
    private final MapCodec<V> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, V> streamCodec;

    public Serializer(
            MapCodec<V> codec,
            StreamCodec<RegistryFriendlyByteBuf, V> streamCodec
    ) {
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    public MapCodec<V> codec() {
        return codec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, V> streamCodec() {
        return streamCodec;
    }
}

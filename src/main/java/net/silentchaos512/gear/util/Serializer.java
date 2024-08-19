package net.silentchaos512.gear.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;

public class Serializer<B, V> {
    private final MapCodec<V> codec;
    private final StreamCodec<B, V> streamCodec;

    public Serializer(
            MapCodec<V> codec,
            StreamCodec<B, V> streamCodec
    ) {
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    public MapCodec<V> codec() {
        return codec;
    }

    public StreamCodec<B, V> streamCodec() {
        return streamCodec;
    }
}

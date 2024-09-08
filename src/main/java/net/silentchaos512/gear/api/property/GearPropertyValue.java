package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public abstract class GearPropertyValue<T> {
    protected final T value;

    public GearPropertyValue(T value) {
        this.value = value;
    }

    public T value() {
        return this.value;
    }

    public static <T, V extends GearPropertyValue<T>> Codec<V> createSimpleValueCodec(
            Codec<T> codec,
            Function<T, V> constructor
    ) {
        return codec.xmap(
                constructor,
                GearPropertyValue::value
        );
    }

    public static <T, V extends GearPropertyValue<T>, B extends FriendlyByteBuf> StreamCodec<B, V> createSimpleStreamCodec(
            StreamCodec<B, T> streamCodec,
            Function<T, V> constructor
    ) {
        return StreamCodec.of(
                (buf, val) -> streamCodec.encode(buf, val.value),
                buf -> constructor.apply(streamCodec.decode(buf))
        );
    }
}

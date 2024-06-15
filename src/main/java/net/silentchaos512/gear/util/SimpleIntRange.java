package net.silentchaos512.gear.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record SimpleIntRange(int min, int max) {
    private static final Codec<SimpleIntRange> RECORD_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.optionalFieldOf("min", 1).forGetter(r -> r.min),
                            Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(r -> r.max)
                    )
                    .apply(instance, SimpleIntRange::new)
    );
    public static final Codec<SimpleIntRange> CODEC = Codec.either(Codec.INT, RECORD_CODEC)
            .xmap(either -> either.map(k -> new SimpleIntRange(k, k), Function.identity()), simpleIntRange -> {
                return simpleIntRange.min == simpleIntRange.max ? Either.left(simpleIntRange.min) : Either.right(simpleIntRange);
            });

    public boolean test(int value) {
        return value >= min && value <= max;
    }
}

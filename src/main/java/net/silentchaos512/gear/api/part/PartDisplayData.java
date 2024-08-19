package net.silentchaos512.gear.api.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record PartDisplayData(
        Component name,
        Component namePrefix
) {
    public static final Codec<PartDisplayData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(d -> d.name),
                    ComponentSerialization.CODEC.optionalFieldOf("name_prefix").forGetter(d -> Optional.ofNullable(d.namePrefix))
            ).apply(instance, (name, prefix) -> new PartDisplayData(name, prefix.orElse(Component.empty())))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PartDisplayData> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, d -> d.name,
            ComponentSerialization.STREAM_CODEC, d -> d.namePrefix,
            PartDisplayData::new
    );
}

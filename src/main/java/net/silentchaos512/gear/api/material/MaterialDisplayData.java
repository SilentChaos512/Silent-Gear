package net.silentchaos512.gear.api.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.lib.util.Color;

import java.util.Optional;

public record MaterialDisplayData(
        Component name,
        Component namePrefix,
        Color color,
        TextureType mainTextureType
) {
    public static final Codec<MaterialDisplayData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(d -> d.name),
                    ComponentSerialization.CODEC.optionalFieldOf("name_prefix").forGetter(d -> Optional.ofNullable(d.namePrefix)),
                    Color.CODEC.fieldOf("color").forGetter(d -> d.color),
                    TextureType.CODEC.fieldOf("main_texture_type").forGetter(d -> d.mainTextureType)
            ).apply(instance, (name, prefix, color, textureType) ->
                    new MaterialDisplayData(name, prefix.orElse(Component.empty()), color, textureType)
            )
    );

    // TODO: Remove in 1.21: Silent Lib adds the stream codec
    private static final StreamCodec<FriendlyByteBuf, Color> TEMP_COLOR_STREAM_CODEC = StreamCodec.of(
            (buf, color) -> buf.writeVarInt(color.getColor()),
            buf -> new Color(buf.readVarInt())
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialDisplayData> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, d -> d.name,
            ComponentSerialization.STREAM_CODEC, d -> d.namePrefix,
            TEMP_COLOR_STREAM_CODEC, d -> d.color,
            TextureType.STREAM_CODEC, d -> d.mainTextureType,
            MaterialDisplayData::new
    );
}

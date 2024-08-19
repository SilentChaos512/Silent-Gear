package net.silentchaos512.gear.api.material;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.util.Serializer;

public class MaterialSerializer<T extends Material> extends Serializer<RegistryFriendlyByteBuf, T> {
    public MaterialSerializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec, streamCodec);
    }
}

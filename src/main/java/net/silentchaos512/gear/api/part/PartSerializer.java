package net.silentchaos512.gear.api.part;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.util.Serializer;

public class PartSerializer<T extends GearPart> extends Serializer<RegistryFriendlyByteBuf, T> {
    public PartSerializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec, streamCodec);
    }
}

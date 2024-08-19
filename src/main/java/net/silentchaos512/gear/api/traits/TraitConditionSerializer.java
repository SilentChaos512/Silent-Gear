package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.util.Serializer;

public class TraitConditionSerializer<V extends ITraitCondition> extends Serializer<RegistryFriendlyByteBuf, V> {
    public TraitConditionSerializer(MapCodec<V> codec, StreamCodec<RegistryFriendlyByteBuf, V> streamCodec) {
        super(codec, streamCodec);
    }

    public StreamCodec<RegistryFriendlyByteBuf, ITraitCondition> getRawStreamCodec() {
        //noinspection unchecked
        return (StreamCodec<RegistryFriendlyByteBuf, ITraitCondition>) streamCodec();
    }
}

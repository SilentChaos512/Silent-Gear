package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.util.Serializer;

public class TraitEffectType<T extends TraitEffect> extends Serializer<RegistryFriendlyByteBuf, T> {
    public TraitEffectType(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec, streamCodec);
    }

    public StreamCodec<RegistryFriendlyByteBuf, TraitEffect> rawStreamCodec() {
        //noinspection unchecked
        return (StreamCodec<RegistryFriendlyByteBuf, TraitEffect>) streamCodec();
    }
}

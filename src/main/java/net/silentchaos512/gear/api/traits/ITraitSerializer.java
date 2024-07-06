package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface ITraitSerializer<T extends ITrait> {
    Codec<T> codec();

    StreamCodec<FriendlyByteBuf, T> streamCodec();

    ResourceLocation getName();
}

package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GearPropertyType<V extends GearPropertyValue<?>>(
        Codec<V> codec,
        StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec
) {
}

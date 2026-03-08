package net.silentchaos512.gear.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.gear.material.MaterialInstance;

/**
 * Used to represent partial amounts of material for repair kits
 */
public record PartialMaterialStorage(
        MaterialInstance material,
        float amount
) {
    public static final Codec<PartialMaterialStorage> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    MaterialInstance.CODEC.fieldOf("material").forGetter(o -> o.material),
                    Codec.FLOAT.fieldOf("amount").forGetter(o -> o.amount)
            ).apply(instance, PartialMaterialStorage::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PartialMaterialStorage> STREAM_CODEC = StreamCodec.composite(
            MaterialInstance.STREAM_CODEC, o -> o.material,
            ByteBufCodecs.FLOAT, o -> o.amount,
            PartialMaterialStorage::new
    );
}

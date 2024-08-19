package net.silentchaos512.gear.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.gear.material.MaterialInstance;

public record PartItemData(MaterialInstance material, int materialCount) {
    public static final Codec<PartItemData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    MaterialInstance.CODEC.fieldOf("material").forGetter(d -> d.material),
                    Codec.INT.fieldOf("material_count").forGetter(d -> d.materialCount)
            ).apply(instance, PartItemData::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, PartItemData> STREAM_CODEC = StreamCodec.composite(
            MaterialInstance.STREAM_CODEC, d -> d.material,
            ByteBufCodecs.VAR_INT, d -> d.materialCount,
            PartItemData::new
    );
}

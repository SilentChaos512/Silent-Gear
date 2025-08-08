package net.silentchaos512.gear.api.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public record MaterialEquippableInfo(
        Holder<SoundEvent> equipSound,
        ResourceKey<EquipmentAsset> assetId,
        ResourceKey<EquipmentAsset> compoundAssetId,
        boolean alwaysUseTint
) {
    public static final Codec<MaterialEquippableInfo> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.SOUND_EVENT.holderByNameCodec().fieldOf("equip_sound").forGetter(d -> d.equipSound),
                    ResourceKey.codec(EquipmentAssets.ROOT_ID).fieldOf("asset_id").forGetter(d -> d.assetId),
                    ResourceKey.codec(EquipmentAssets.ROOT_ID).fieldOf("compound_asset_id").forGetter(d -> d.compoundAssetId),
                    Codec.BOOL.fieldOf("always_use_tint").forGetter(d -> d.alwaysUseTint)
            ).apply(instance, MaterialEquippableInfo::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialEquippableInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), d -> d.equipSound,
            ResourceKey.streamCodec(EquipmentAssets.ROOT_ID), d -> d.assetId,
            ResourceKey.streamCodec(EquipmentAssets.ROOT_ID), d -> d.compoundAssetId,
            ByteBufCodecs.BOOL, d -> d.alwaysUseTint,
            MaterialEquippableInfo::new
    );
}

package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.Serializer;

import java.util.List;

public interface ITraitCondition {
    Codec<ITraitCondition> DISPATCH_CODEC = SgRegistries.TRAIT_CONDITIONS.byNameCodec()
            .dispatch(
                    ITraitCondition::serializer,
                    Serializer::codec
            );
    StreamCodec<RegistryFriendlyByteBuf, TraitConditionSerializer<?>> REGISTRY_STREAM_CODEC = ByteBufCodecs.registry(SgRegistries.TRAIT_CONDITIONS_KEY);
    StreamCodec<RegistryFriendlyByteBuf, ITraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, c) -> {
                REGISTRY_STREAM_CODEC.encode(buf, c.serializer());
                c.serializer().getRawStreamCodec().encode(buf, c);
            },
            buf -> {
                var serializer = REGISTRY_STREAM_CODEC.decode(buf);
                return serializer.streamCodec().decode(buf);
            }
    );

    @Deprecated(forRemoval = true)
    ResourceLocation getId();

    TraitConditionSerializer<?> serializer();

    boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<? extends IGearComponentInstance<?>> components);

    MutableComponent getDisplayText();
}

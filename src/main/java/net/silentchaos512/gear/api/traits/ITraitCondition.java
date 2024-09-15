package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.List;

public interface ITraitCondition {
    @SuppressWarnings("RedundantCast") // Fails to build without casting the codec
    Codec<ITraitCondition> DISPATCH_CODEC = SgRegistries.TRAIT_CONDITION.byNameCodec()
            .dispatch(
                    ITraitCondition::serializer,
                    traitConditionSerializer -> (MapCodec<? extends ITraitCondition>) traitConditionSerializer.codec()
            );
    StreamCodec<RegistryFriendlyByteBuf, TraitConditionSerializer<?>> REGISTRY_STREAM_CODEC = ByteBufCodecs.registry(SgRegistries.TRAIT_CONDITION_KEY);
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

    TraitConditionSerializer<?> serializer();

    boolean matches(Trait trait, PartGearKey key, List<? extends GearComponentInstance<?>> components);

    MutableComponent getDisplayText();
}

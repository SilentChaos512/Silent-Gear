package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.property.ComputeContext;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Optional;

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

    boolean matches(Trait trait, ComputeContext context);

    /**
     * Filters out trait conditions that are no longer relevant. Note this method is called after the conditions
     * attached to the trait are determined to match with {@link #matches(Trait, ComputeContext)}.
     *
     * @param trait   The trait
     * @param context The context
     * @return A new trait condition to replace the existing one, or an empty optional to remove it
     */
    Optional<ITraitCondition> reduce(Trait trait, ComputeContext context);

    MutableComponent getDisplayText();
}

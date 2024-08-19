package net.silentchaos512.gear.gear.trait.effect;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CancelEffectsTraitEffect extends TraitEffect {
    public static final MapCodec<CancelEffectsTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.list(BuiltInRegistries.MOB_EFFECT.holderByNameCodec())
                            .fieldOf("cleared_effects")
                            .forGetter(e -> e.effects)
            ).apply(instance, CancelEffectsTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CancelEffectsTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT).apply(ByteBufCodecs.list()), e -> e.effects,
            CancelEffectsTraitEffect::new
    );

    private final List<Holder<MobEffect>> effects;

    public CancelEffectsTraitEffect(List<Holder<MobEffect>> effects) {
        this.effects = ImmutableList.copyOf(effects);
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.CANCEL_EFFECTS.get();
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (isEquipped) {
            Player player = context.player();
            if (player != null) {
                for (var effect : this.effects) {
                    player.removeEffect(effect);
                }
            }
        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        ret.add("  - Cancels these effects: " +
                this.effects.stream()
                        .map(e -> "`" + BuiltInRegistries.MOB_EFFECT.getKey(e.value()) + "`")
                        .collect(Collectors.joining(", "))
        );
        return ret;
    }
}

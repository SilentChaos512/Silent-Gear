package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WindBlastTraitEffect extends TraitEffect {
    public static final WindBlastTraitEffect INSTANCE = new WindBlastTraitEffect();
    public static final MapCodec<WindBlastTraitEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, WindBlastTraitEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final SimpleExplosionDamageCalculator DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
            true, false, Optional.of(1.22F), BuiltInRegistries.BLOCK.get(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.WIND_BLAST.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of("Creates an explosion similar to a wind charge on attack");
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        target.level()
                .explode(
                        null,
                        null,
                        DAMAGE_CALCULATOR,
                        target.position().x(),
                        target.position().y(),
                        target.position().z(),
                        1.2F,
                        false,
                        Level.ExplosionInteraction.TRIGGER,
                        ParticleTypes.GUST_EMITTER_SMALL,
                        ParticleTypes.GUST_EMITTER_LARGE,
                        WeightedList.of(),
                        SoundEvents.WIND_CHARGE_BURST
                );
        return super.onAttackEntity(context, target, baseValue);
    }
}

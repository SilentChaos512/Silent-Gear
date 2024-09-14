package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.SgCriteriaTriggers;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.lib.util.MathUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A trait effect that modifies durability damage taken by gear, similar to the unbreaking
 * enchantment (or the reverse in some cases). Chance of modification increases with trait level.
 * Effect scale does not change with level. A negative scale reduces damage taken, positive
 * increases it.
 */
public final class DurabilityTraitEffect extends TraitEffect {
    public static final MapCodec<DurabilityTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("activation_chance").forGetter(e -> e.activationChance),
                    Codec.INT.fieldOf("effect_scale").forGetter(e -> e.effectScale)
            ).apply(instance, DurabilityTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, DurabilityTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, e -> e.activationChance,
            ByteBufCodecs.VAR_INT, e -> e.effectScale,
            DurabilityTraitEffect::new
    );

    private final float activationChance;
    private final int effectScale;

    public DurabilityTraitEffect(float activationChance, int effectScale) {
        this.activationChance = activationChance;
        this.effectScale = effectScale;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.DURABILITY.get();
    }

    private boolean shouldActivate(int level) {
        return MathUtils.tryPercentage(activationChance * level);
    }

    @Override
    public int onDurabilityDamage(TraitActionContext context, int damageTaken) {
        Player player = context.player();
        if (damageTaken != 0 && shouldActivate(context.traitLevel())) {
            if (effectScale > 0 && player instanceof ServerPlayer) {
                SgCriteriaTriggers.BRITTLE_DAMAGE.get().trigger((ServerPlayer) player);
            }
            return damageTaken + effectScale;
        }

        return super.onDurabilityDamage(context, damageTaken);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        int chancePercent = (int) (100 * activationChance);
        String line = String.format("  - %d damage with a %d%% chance per level", effectScale, chancePercent);
        ret.add(line);
        return ret;
    }
}

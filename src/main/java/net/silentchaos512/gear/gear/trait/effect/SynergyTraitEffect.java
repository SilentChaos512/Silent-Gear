package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.ArrayList;
import java.util.Collection;

public final class SynergyTraitEffect extends TraitEffect {
    public static final MapCodec<SynergyTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(e -> e.multi),
                    Codec.FLOAT.optionalFieldOf("range_min", 0.0f).forGetter(e -> e.rangeMin),
                    Codec.FLOAT.optionalFieldOf("range_max", Float.MAX_VALUE).forGetter(e -> e.rangeMax)
            ).apply(instance, SynergyTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SynergyTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, e -> e.multi,
            ByteBufCodecs.FLOAT, e -> e.rangeMin,
            ByteBufCodecs.FLOAT, e -> e.rangeMax,
            SynergyTraitEffect::new
    );

    private final float multi;
    private final float rangeMin;
    private final float rangeMax;

    public SynergyTraitEffect(float multi) {
        this(multi, 0.0f, Float.MAX_VALUE);
    }

    public SynergyTraitEffect(float multi, float rangeMin, float rangeMax) {
        this.multi = multi;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.SYNERGY_MULTIPLIER.get();
    }

    @Override
    public double onCalculateSynergy(double synergy, int traitLevel) {
        if (synergy > rangeMin && synergy < rangeMax) {
            return synergy + traitLevel * multi;
        }
        return synergy;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        ret.add("  - Please read [this page](https://github.com/SilentChaos512/Silent-Gear/wiki/Synergy) for more information on synergy");
        String multiStr = "  - " + (multi > 0f ? "+" + multi : String.valueOf(multi));
        String str;
        if (rangeMax < Float.MAX_VALUE) {
            str = multiStr + " synergy per level if between " + formatPercent(rangeMin) + " and " + formatPercent(rangeMax);
        } else {
            str = multiStr + " synergy per level if greater than " + formatPercent(rangeMin);
        }
        ret.add(str);
        return ret;
    }

    private static String formatPercent(float value) {
        return (int) (value * 100) + "%";
    }
}

package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.CodecUtils;

import java.util.Collection;
import java.util.List;

public class NegateDamageTraitEffect extends TraitEffect {
    public static final MapCodec<NegateDamageTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TagKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type_tag").forGetter(e -> e.damageType),
                    Codec.FLOAT.fieldOf("negated_damage_scale").forGetter(e -> e.negatedDamageScale)
            ).apply(instance, NegateDamageTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, NegateDamageTraitEffect> STREAM_CODEC = StreamCodec.composite(
            CodecUtils.tagStreamCodec(Registries.DAMAGE_TYPE), e -> e.damageType,
            ByteBufCodecs.FLOAT, e -> e.negatedDamageScale,
            NegateDamageTraitEffect::new
    );

    private final TagKey<DamageType> damageType;
    private final float negatedDamageScale;

    public NegateDamageTraitEffect(TagKey<DamageType> damageType, float negatedDamageScale) {
        this.damageType = damageType;
        this.negatedDamageScale = negatedDamageScale;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.NEGATE_DAMAGE.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of(
                String.format(
                        "  - Reduces \"%s\" type damage by %d%% per level per armor piece",
                        this.damageType.location(),
                        (int) (this.negatedDamageScale * 100)
                )
        );
    }

    @Override
    public float onEntityIncomingDamage(ItemStack armor, int traitLevel, LivingEntity target, DamageSource source, float amount, float originalAmount) {
        if (source.is(this.damageType)) {
            float reduction = originalAmount * this.negatedDamageScale * traitLevel;
            return Mth.clamp(amount - reduction, 0f, amount);
        }
        return amount;
    }
}

package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.CodecUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class ExtraDamageTraitEffect extends TraitEffect {
    public static final MapCodec<ExtraDamageTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("bonus_damage_per_level").forGetter(e -> e.bonusDamagePerLevel),
                    TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("affected_entities_tag", null).forGetter(e -> e.affectedEntitiesTag),
                    Codec.BOOL.optionalFieldOf("affects_fire_immune_mobs", false).forGetter(e -> e.affectsFireImmune)
            ).apply(instance, ExtraDamageTraitEffect::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraDamageTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, e -> e.bonusDamagePerLevel,
            CodecUtils.tagStreamCodec(Registries.ENTITY_TYPE), e -> e.affectedEntitiesTag,
            ByteBufCodecs.BOOL, e -> e.affectsFireImmune,
            ExtraDamageTraitEffect::new
    );

    private final float bonusDamagePerLevel;
    @Nullable private final TagKey<EntityType<?>> affectedEntitiesTag;
    private final boolean affectsFireImmune;

    public ExtraDamageTraitEffect(float bonusDamagePerLevel, @Nullable TagKey<EntityType<?>> affectedEntitiesTag, boolean affectsFireImmune) {
        this.bonusDamagePerLevel = bonusDamagePerLevel;
        this.affectedEntitiesTag = affectedEntitiesTag;
        this.affectsFireImmune = affectsFireImmune;
    }

    public static ExtraDamageTraitEffect affecting(TagKey<EntityType<?>> entityTag, float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, entityTag, false);
    }

    public static ExtraDamageTraitEffect affectingFireImmune(float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, null, true);
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.EXTRA_DAMAGE.get();
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        if (isAffectedEntity(target)) {
            return baseValue + bonusDamagePerLevel * context.traitLevel();
        }
        return baseValue;
    }

    private boolean isAffectedEntity(LivingEntity target) {
        return (this.affectsFireImmune && target.fireImmune())
                || (this.affectedEntitiesTag != null && target.getType().is(this.affectedEntitiesTag));
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        // TODO
        return List.of();
    }
}

package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.CodecUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiPredicate;

public class ExtraDamageTraitEffect extends TraitEffect {
    public static final MapCodec<ExtraDamageTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("bonus_damage_per_level").forGetter(e -> e.bonusDamagePerLevel),
                    TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("affected_entities_tag").forGetter(e -> Optional.ofNullable(e.affectedEntitiesTag)),
                    AffectedMobTypes.CODEC.fieldOf("affected_type").forGetter(e -> e.affectedMobTypes)
            ).apply(instance, (bonusDamagePerLevel1, affectedEntitiesTag1, affectedMobTypes1) ->
                    new ExtraDamageTraitEffect(bonusDamagePerLevel1, affectedEntitiesTag1.orElse(null), affectedMobTypes1))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraDamageTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, e -> e.bonusDamagePerLevel,
            CodecUtils.tagStreamCodec(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional), e -> Optional.ofNullable(e.affectedEntitiesTag),
            AffectedMobTypes.STREAM_CODEC, e -> e.affectedMobTypes,
            (bonusDamagePerLevel1, affectedEntitiesTag1, affectedMobTypes1) ->
                    new ExtraDamageTraitEffect(bonusDamagePerLevel1, affectedEntitiesTag1.orElse(null), affectedMobTypes1)
    );

    private final float bonusDamagePerLevel;
    @Nullable
    private final TagKey<EntityType<?>> affectedEntitiesTag;
    private final AffectedMobTypes affectedMobTypes;

    public ExtraDamageTraitEffect(float bonusDamagePerLevel, @Nullable TagKey<EntityType<?>> affectedEntitiesTag, AffectedMobTypes affectedMobTypes) {
        this.bonusDamagePerLevel = bonusDamagePerLevel;
        this.affectedEntitiesTag = affectedEntitiesTag;
        this.affectedMobTypes = affectedMobTypes;
    }

    public static ExtraDamageTraitEffect affecting(TagKey<EntityType<?>> entityTag, float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, entityTag, AffectedMobTypes.TAGGED);
    }

    public static ExtraDamageTraitEffect affectingFireImmune(float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, null, AffectedMobTypes.FIRE_IMMUNE);
    }

    public static ExtraDamageTraitEffect affectingAquatic(float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, null, AffectedMobTypes.AQUATIC);
    }

    public static ExtraDamageTraitEffect affectingAllMobs(float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, null, AffectedMobTypes.ALL);
    }

    public static ExtraDamageTraitEffect affectingHighHealth(float bonusDamagePerLevel) {
        return new ExtraDamageTraitEffect(bonusDamagePerLevel, null, AffectedMobTypes.HIGH_HEALTH);
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
        return this.affectedMobTypes.predicate.test(target, this.affectedEntitiesTag);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        // TODO
        return List.of();
    }

    public enum AffectedMobTypes implements StringRepresentable {
        ALL((entity, tag) -> true),
        TAGGED((entity, tag) -> entity.getType().is(tag)),
        HIGH_HEALTH((entity, tag) -> entity.getMaxHealth() > 21f),
        FIRE_IMMUNE((entity, tag) -> entity.fireImmune()),
        AQUATIC(((entity, tag) -> entity.canDrownInFluidType(NeoForgeMod.WATER_TYPE.value())));

        private final BiPredicate<LivingEntity, TagKey<EntityType<?>>> predicate;

        AffectedMobTypes(BiPredicate<LivingEntity, TagKey<EntityType<?>>> predicate) {
            this.predicate = predicate;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static final Codec<AffectedMobTypes> CODEC = StringRepresentable.fromEnum(AffectedMobTypes::values);
        public static final StreamCodec<FriendlyByteBuf, AffectedMobTypes> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(AffectedMobTypes.class);
    }
}

package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearHelper;

import java.util.*;
import java.util.function.Supplier;

public class TargetEffectTraitEffect extends TraitEffect {
   public static final MapCodec<TargetEffectTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
           instance -> instance.group(
                   Codec.unboundedMap(GearType.CODEC, EffectMap.CODEC)
                           .fieldOf("effects_by_level")
                           .forGetter(e -> e.effects)
           ).apply(instance, TargetEffectTraitEffect::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, TargetEffectTraitEffect> STREAM_CODEC = StreamCodec.composite(
           ByteBufCodecs.map(
                   HashMap::new,
                   GearType.STREAM_CODEC,
                   EffectMap.STREAM_CODEC
           ), e -> e.effects,
           TargetEffectTraitEffect::new
   );

    private final Map<GearType, EffectMap> effects = new LinkedHashMap<>();

    public TargetEffectTraitEffect(Map<GearType, EffectMap> effects) {
        this.effects.putAll(effects);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.TARGET_EFFECT.get();
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        var typeOfGear = GearHelper.getType(context.gear());
        for (var gearTypeInMap : this.effects.keySet()) {
            if (typeOfGear.matches(gearTypeInMap)) {
                this.effects.get(gearTypeInMap).applyTo(target, context.traitLevel());
            }
        }
        return super.onAttackEntity(context, target, baseValue);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        this.effects.forEach((type, map) -> {
            ret.add("  - " + type);
            ret.addAll(map.getWikiLines());
        });
        return ret;
    }

    public static class EffectMap {
        private static final Codec<Integer> KEY_CODEC = Codec.STRING.comapFlatMap(
                str -> {
                    try {
                        int value = Integer.parseInt(str);
                        if (value > 0) {
                            return DataResult.success(value);
                        }
                        return DataResult.error(() -> "Level key must be positive: " + value);
                    } catch (NumberFormatException ex) {
                        return DataResult.error(() -> "Not a number: " + str);
                    }
                },
                Object::toString
        );

        public static final Codec<EffectMap> CODEC = Codec.unboundedMap(KEY_CODEC, Codec.list(MobEffectInstance.CODEC))
                .xmap(
                        EffectMap::new,
                        effectMap -> effectMap.effects
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, EffectMap> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(
                        HashMap::new,
                        ByteBufCodecs.VAR_INT,
                        MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list())
                ), map -> map.effects,
                EffectMap::new
        );

        private final Map<Integer, List<MobEffectInstance>> effects = new LinkedHashMap<>();

        public EffectMap() {
        }

        public EffectMap(Map<Integer, List<MobEffectInstance>> effects) {
            this.effects.putAll(effects);
        }

        public void applyTo(LivingEntity target, int traitLevel) {
            if (this.effects.containsKey(traitLevel)) {
                for (MobEffectInstance effect : this.effects.get(traitLevel)) {
                    MobEffectInstance copy = new MobEffectInstance(effect);
                    target.addEffect(copy);
                }
            }
        }

        public Collection<String> getWikiLines() {
            Collection<String> ret = new ArrayList<>();
            effects.forEach((level, list) -> {
                ret.add("    - Level " + level + ":");
                list.forEach(effect -> {
                    ret.add("      - " + effect);
                });
            });
            return ret;
        }
    }

    public static class Builder {
        private final Map<GearType, EffectMap> map = new LinkedHashMap<>();

        public Builder add(Supplier<GearType> gearType, int traitLevel, MobEffectInstance effect) {
            var effectMap = this.map.computeIfAbsent(gearType.get(), gt -> new EffectMap());
            var effectList = effectMap.effects.computeIfAbsent(traitLevel, lvl -> new ArrayList<>());
            effectList.add(effect);
            return this;
        }

        public Builder addWithDurationByLevel(Supplier<GearType> gearType, Holder<MobEffect> effect, int maxLevel, float baseDurationInSeconds) {
            int baseDurationInTicks = (int) (baseDurationInSeconds * 20);
            for (var level = 1; level <= maxLevel; ++level) {
                add(gearType, level, new MobEffectInstance(effect, baseDurationInTicks * level));
            }
            return this;
        }

        public TargetEffectTraitEffect build() {
            return new TargetEffectTraitEffect(this.map);
        }
    }
}

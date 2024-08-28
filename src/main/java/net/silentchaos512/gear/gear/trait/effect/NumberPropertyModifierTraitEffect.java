package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public final class NumberPropertyModifierTraitEffect extends TraitEffect {
    private static final Codec<NumberProperty> KEY_CODEC = SgRegistries.GEAR_PROPERTY.byNameCodec()
            .comapFlatMap(
                    property -> {
                        if (property instanceof NumberProperty numberProperty) {
                            return DataResult.success(numberProperty);
                        }
                        return DataResult.error(() -> "Not a NumberProperty: " + SgRegistries.GEAR_PROPERTY.getKey(property));
                    },
                    numberProperty -> numberProperty
            );

    public static final MapCodec<NumberPropertyModifierTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.unboundedMap(KEY_CODEC, StatMod.CODEC)
                            .fieldOf("property_modifiers")
                            .forGetter(e -> e.mods)
            ).apply(instance, NumberPropertyModifierTraitEffect::new)
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, NumberProperty> KEY_STREAM_CODEC = StreamCodec.of(
            (buf, val) -> buf.writeResourceLocation(Objects.requireNonNull(SgRegistries.GEAR_PROPERTY.getKey(val))),
            buf -> (NumberProperty) SgRegistries.GEAR_PROPERTY.get(buf.readResourceLocation())
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, NumberPropertyModifierTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    KEY_STREAM_CODEC,
                    StatMod.STREAM_CODEC
            ), e -> e.mods,
            NumberPropertyModifierTraitEffect::new
    );

    private final Map<NumberProperty, StatMod> mods = new LinkedHashMap<>();

    public NumberPropertyModifierTraitEffect(Map<NumberProperty, StatMod> modsIn) {
        this.mods.putAll(modsIn);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.NUMBER_PROPERTY_MODIFIER.get();
    }

    @Override
    public Collection<GearPropertyValue<?>> getBonusProperties(
            int traitLevel,
            @Nullable Player player,
            GearProperty<?, ?> property,
            GearPropertyValue<?> baseValue,
            float damageRatio
    ) {
        if (property instanceof NumberProperty numberProperty) {
            var propertyMod = this.mods.get(property);
            if (propertyMod != null) {
                var numberPropertyValue = (NumberPropertyValue) baseValue;
                var bonusValue = propertyMod.getAddedValue(traitLevel, numberPropertyValue.value(), damageRatio);
                return List.of(new NumberPropertyValue(bonusValue, NumberProperty.Operation.ADD));
            }
        }
        return super.getBonusProperties(traitLevel, player, property, baseValue, damageRatio);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        this.mods.forEach((stat, mod) -> {
            ret.add("  - " + stat.getDisplayName().getString() + ": " + mod.multiplier
                    + " * level"
                    + (mod.multiplyDamageRatio ? " * damage" : "")
                    + (mod.multiplyOriginalValue ? " * value" : ""));
        });
        return ret;
    }

    public record StatMod(
            float multiplier,
            boolean multiplyDamageRatio,
            boolean multiplyOriginalValue
    ) {
        public static final Codec<StatMod> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.FLOAT.fieldOf("base_multiplier").forGetter(m -> m.multiplier),
                        Codec.BOOL.fieldOf("multiply_damage_ratio").forGetter(m -> m.multiplyDamageRatio),
                        Codec.BOOL.fieldOf("multiply_original_value").forGetter(m -> m.multiplyOriginalValue)
                ).apply(instance, StatMod::new)
        );

        public static final StreamCodec<FriendlyByteBuf, StatMod> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, m -> m.multiplier,
                ByteBufCodecs.BOOL, m -> m.multiplyDamageRatio,
                ByteBufCodecs.BOOL, m -> m.multiplyOriginalValue,
                StatMod::new
        );

        private float getAddedValue(int traitLevel, float originalValue, float damageRatio) {
            float addedValue = this.multiplier * traitLevel;

            if (multiplyDamageRatio)
                addedValue *= damageRatio;
            if (multiplyOriginalValue)
                addedValue *= originalValue;

            return addedValue;
        }
    }

    public static final class Builder {
        private final Map<NumberProperty, StatMod> map = new LinkedHashMap<>();

        public Builder add(Supplier<NumberProperty> property, float multiplier, boolean multiplyDamageRatio, boolean multiplyOriginalValue) {
            map.put(property.get(), new StatMod(multiplier, multiplyDamageRatio, multiplyOriginalValue));
            return this;
        }

        public NumberPropertyModifierTraitEffect build() {
            return new NumberPropertyModifierTraitEffect(this.map);
        }
    }
}

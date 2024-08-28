package net.silentchaos512.gear.gear.trait.effect;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class WielderEffectTraitEffect extends TraitEffect {
    public static final MapCodec<WielderEffectTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.unboundedMap(GearType.CODEC, Codec.list(PotionData.CODEC))
                            .fieldOf("potion_effects")
                            .forGetter(e -> e.potions)
            ).apply(instance, WielderEffectTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, WielderEffectTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    GearType.STREAM_CODEC,
                    PotionData.STREAM_CODEC.apply(ByteBufCodecs.list())
            ), e -> e.potions,
            WielderEffectTraitEffect::new
    );

    private final Map<GearType, List<PotionData>> potions;

    public WielderEffectTraitEffect(Map<GearType, List<PotionData>> potions) {
        this.potions = ImmutableMap.copyOf(potions);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.WIELDER_EFFECT.get();
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (!isEquipped || context.player() == null || context.player().tickCount % 10 != 0) return;

        GearType gearType = ((ICoreItem) context.gear().getItem()).getGearType();
        potions.forEach((type, list) -> {
            applyEffects(context, gearType, type, list);
        });
    }

    private void applyEffects(TraitActionContext context, GearType gearType, GearType entryType, Iterable<PotionData> effects) {
        Player player = context.player();
        assert player != null; // checked in onUpdate

        if (gearType.matches(entryType, true)) {
            int setPieceCount = getSetPieceCount(context, entryType, player);
            boolean hasFullSet = !"armor".equals(entryType) || setPieceCount >= 4;

            for (PotionData potionData : effects) {
                MobEffectInstance effect = potionData.getEffect(context.traitLevel(), setPieceCount, hasFullSet);
                if (effect != null) {
                    player.addEffect(effect);
                }
            }
        }
    }

    private int getSetPieceCount(TraitActionContext context, GearType type, Player player) {
        if (!type.matches(GearTypes.ARMOR.get())) return 1;

        int count = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof ICoreArmor && TraitHelper.hasTrait(stack, context.trait())) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        this.potions.forEach((type, list) -> {
            ret.add("  - " + type);
            list.forEach(mod -> {
                ret.add("    - " + mod.getWikiLine());
            });
        });
        return ret;
    }

    public static class PotionData {
        public static final Codec<PotionData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        LevelType.CODEC.fieldOf("type").forGetter(d -> d.type),
                        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(d -> d.effect),
                        Codec.INT.fieldOf("duration").forGetter(d -> d.duration),
                        Codec.list(ExtraCodecs.POSITIVE_INT).fieldOf("levels").forGetter(d -> d.levels)
                ).apply(instance, PotionData::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, PotionData> STREAM_CODEC = StreamCodec.composite(
                LevelType.STREAM_CODEC, d -> d.type,
                ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), d -> d.effect,
                ByteBufCodecs.VAR_INT, d -> d.duration,
                ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), d -> d.levels,
                PotionData::new
        );

        private final LevelType type;
        private final Holder<MobEffect> effect;
        private final int duration;
        private final List<Integer> levels;

        public PotionData(LevelType type, Holder<MobEffect> effect, int duration, List<Integer> levels) {
            this.type = type;
            this.effect = effect;
            this.duration = duration;
            this.levels = levels;
        }

        @Deprecated
        public static PotionData of(boolean requiresFullSet, Holder<MobEffect> effect, int... levels) {
            return of(requiresFullSet ? LevelType.FULL_SET_ONLY : LevelType.PIECE_COUNT, effect, levels);
        }

        public static PotionData of(LevelType type, Holder<MobEffect> effect, int... levels) {
            var duration = TimeUtils.ticksFromSeconds(getDefaultDuration(effect));
            return new PotionData(type, effect, duration, Arrays.stream(levels).boxed().toList());
        }

        private static float getDefaultDuration(Holder<MobEffect> effect) {
            // Duration in seconds. The .9 should prevent flickering.
            var nightVision = ResourceLocation.withDefaultNamespace("night_vision");
            return effect.is(nightVision) ? 15.9f : 1.9f;
        }

        @Nullable
        MobEffectInstance getEffect(int traitLevel, int pieceCount, boolean hasFullSet) {
            if (this.type == LevelType.FULL_SET_ONLY && !hasFullSet) return null;

            int effectLevel = getEffectLevel(traitLevel, pieceCount, hasFullSet);
            if (effectLevel < 1) return null;

            return new MobEffectInstance(this.effect, this.duration, effectLevel - 1, true, false);
        }

        int getEffectLevel(int traitLevel, int pieceCount, boolean hasFullSet) {
            return switch (this.type) {
                case TRAIT_LEVEL -> this.levels.get(Mth.clamp(traitLevel - 1, 0, this.levels.size() - 1));
                case PIECE_COUNT -> this.levels.get(Mth.clamp(pieceCount - 1, 0, this.levels.size() - 1));
                case FULL_SET_ONLY -> this.levels.getFirst();
            };
        }

        public String getWikiLine() {
            String[] levelsText = new String[levels.size()];
            for (int i = 0; i < levels.size(); ++i) {
                levelsText[i] = Integer.toString(levels.get(i));
            }

            String effectName = effect.value().getDisplayName().getString();

            return String.format("%s: [%s] (%s)", effectName, String.join(", ", levelsText), type.wikiText);
        }
    }

    public enum LevelType {
        TRAIT_LEVEL("by trait level"),
        PIECE_COUNT("by armor piece count"),
        FULL_SET_ONLY("requires full set of armor");

        final String wikiText;

        LevelType(String wikiText) {
            this.wikiText = wikiText;
        }

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Nullable
        public static LevelType byName(String name) {
            for (LevelType type : values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }

        public static final Codec<LevelType> CODEC = Codec.STRING.comapFlatMap(
                s -> {
                    var type = LevelType.byName(s);
                    return type != null
                            ? DataResult.success(type)
                            : DataResult.error(() -> "Unknown WielderEffectTraitEffect.LevelType: " + s);
                },
                type -> type.name().toLowerCase(Locale.ROOT)
        );
        public static final StreamCodec<FriendlyByteBuf, LevelType> STREAM_CODEC = StreamCodec.of(
                (buf, type) -> buf.writeVarInt(type.ordinal()),
                buf -> LevelType.values()[buf.readVarInt()]
        );
    }

    public static class Builder {
        private final Map<GearType, List<PotionData>> effects = new LinkedHashMap<>();

        public Builder add(Supplier<GearType> gearType, PotionData effect) {
            this.effects.computeIfAbsent(gearType.get(), gt -> new ArrayList<>()).add(effect);
            return this;
        }

        public Builder add(Supplier<GearType> gearType, LevelType levelType, Holder<MobEffect> effect, int... levels) {
            var potionData = PotionData.of(levelType, effect, levels);
            this.effects.computeIfAbsent(gearType.get(), gt -> new ArrayList<>()).add(potionData);
            return this;
        }

        public WielderEffectTraitEffect build() {
            return new WielderEffectTraitEffect(this.effects);
        }
    }
}

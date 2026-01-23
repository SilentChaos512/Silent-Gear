package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.silentchaos512.gear.api.event.GearNamePrefixesEvent;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OxidationTraitEffect extends TraitEffect {
    public static final MapCodec<OxidationTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    DataResource.TRAIT_CODEC.optionalFieldOf("next_oxidation_trait").forGetter(e -> e.nextOxidationTrait),
                    Codec.list(Codec.INT, 1, 100).fieldOf("counter_times_per_stage").forGetter(e -> e.counterTimesPerLevel)
            ).apply(instance, OxidationTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OxidationTraitEffect> STREAM_CODEC = StreamCodec.composite(
            DataResource.TRAIT_STREAM_CODEC.apply(ByteBufCodecs::optional), e -> e.nextOxidationTrait,
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), e -> e.counterTimesPerLevel,
            OxidationTraitEffect::new
    );

    private final Optional<DataResource<Trait>> nextOxidationTrait;
    private final List<Integer> counterTimesPerLevel;

    protected OxidationTraitEffect(Optional<DataResource<Trait>> nextOxidationTrait, List<Integer> counterTimesPerLevel) {
        this.nextOxidationTrait = nextOxidationTrait;
        this.counterTimesPerLevel = counterTimesPerLevel;
    }

    public static OxidationTraitEffect create(@Nullable DataResource<Trait> nextOxidationTrait, int... counterTimesInSecondsPerLevel) {
        return new OxidationTraitEffect(Optional.ofNullable(nextOxidationTrait), Arrays.stream(counterTimesInSecondsPerLevel).boxed().toList());
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.OXIDATION.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of();
    }

    @Override
    public Optional<TraitInstance> transformTrait(ItemStack gear, Trait trait, int traitLevel) {
        byte stage = gear.getOrDefault(SgDataComponents.OXIDATION_STAGE, (byte) 0);
        if (stage == 0) {
            return Optional.empty();
        } else if (this.nextOxidationTrait.isPresent()) {
            if (stage >= trait.getMaxLevel()) {
                gear.set(SgDataComponents.OXIDATION_STAGE, (byte) 1);
                return Optional.of(TraitInstance.of(this.nextOxidationTrait.get(), stage));
            }
        }
        return Optional.of(TraitInstance.of(trait, stage));
    }

    @Override
    public void inventoryTick(TraitActionContext context, Level level, Entity entity, boolean isEquipped) {
        // Tick only once per second or if oxidation is possible
        // Returning on isEquipped prevents the "bobbing" animation from playing constantly
        if (isEquipped || level.isClientSide() || entity.tickCount % 20 != 0 || !canOxidize(context, level)) {
            return;
        }

        ItemStack gear = context.gear();
        // No need to tick counter if oxidation is maxed out
        byte stage = gear.getOrDefault(SgDataComponents.OXIDATION_STAGE, (byte) 0);
        if (stage >= context.trait().getMaxLevel() && this.nextOxidationTrait.isEmpty()) {
            return;
        }

        int counter = gear.getOrDefault(SgDataComponents.OXIDATION_COUNTER, 0);
        ++counter;
        if (counter >= getStageTime(context.trait(), context.traitLevel())) {
            counter = 0;
            if (stage < context.trait().getMaxLevel()) {
                ++stage;
            }
            gear.set(SgDataComponents.OXIDATION_STAGE, stage);
            gear.set(SgDataComponents.RECALCULATE_FLAG, Unit.INSTANCE);
        }
        gear.set(SgDataComponents.OXIDATION_COUNTER, counter);
    }

    @Override
    public void onBlockBreak(TraitActionContext context, BlockEvent.BreakEvent event) {
        int reductionAmount = getStageTime(context.trait(), context.traitLevel()) / 5 + 1;
        reduceOxidationCounter(reductionAmount, context.gear(), context.trait(), context.traitLevel());
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        int reductionAmount = getStageTime(context.trait(), context.traitLevel()) / 5 + 1;
        reduceOxidationCounter(reductionAmount, context.gear(), context.trait(), context.traitLevel());
        return super.onAttackEntity(context, target, baseValue);
    }

    protected boolean canOxidize(TraitActionContext context, Level level) {
        return !GearData.hasPartOfType(context.gear(), PartTypes.COATING.get());
    }

    private int getStageTime(Trait trait, int traitLevel) {
        return this.counterTimesPerLevel.get(Mth.clamp(traitLevel - 1, 0, this.counterTimesPerLevel.size()));
    }

    private void reduceOxidationCounter(int amount, ItemStack gear, Trait trait, int traitLevel) {
        byte stage = gear.getOrDefault(SgDataComponents.OXIDATION_STAGE, (byte) 0);
        int counter = gear.getOrDefault(SgDataComponents.OXIDATION_COUNTER, 0);

        counter -= amount;
        if (counter < 0 && stage > 1) {
            --stage;
            gear.set(SgDataComponents.OXIDATION_STAGE, stage);
            counter = counter + getStageTime(trait, stage);
        }

        int clamped = Mth.clamp(counter, 0, getStageTime(trait, stage));
        gear.set(SgDataComponents.OXIDATION_COUNTER, clamped);
    }

    @EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onGearNamePrefixes(GearNamePrefixesEvent event) {
            // FIXME: Causes infinite recursion trying to get gear properties on dummy items?
            //  Possible solution: Add a marker data component to these items to prevent infinite recursion
            //  Maybe add gear properties to GearItemEvent as well.
//            var gear = event.getGear();
//            if (TraitHelper.hasTrait(gear, Const.Traits.OXIDIZED)) {
//                int traitLevel = TraitHelper.getTraitLevel(gear, Const.Traits.OXIDIZED);
//                event.getPrefixes().add(Component.translatable("trait.silentgear.oxidized.prefix" + traitLevel));
//            }
        }
    }
}

package net.silentchaos512.gear.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Optional;

public class GearPropertyTrigger extends SimpleCriterionTrigger<GearPropertyTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, GearProperty<?, ?> stat, double value) {
        this.trigger(player, instance -> instance.matches(stat, value));
    }

    public record Instance(
            Optional<ContextAwarePredicate> player,
            GearProperty<?, ?> stat,
            MinMaxBounds.Doubles value
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                        SgRegistries.GEAR_PROPERTY.byNameCodec().fieldOf("stat").forGetter(Instance::stat),
                        MinMaxBounds.Doubles.CODEC.fieldOf("value").forGetter(Instance::value)
                ).apply(instance, Instance::new)
        );

        public boolean matches(GearProperty<?, ?> statIn, double valueIn) {
            return this.stat == statIn && this.value.matches(valueIn);
        }
    }
}

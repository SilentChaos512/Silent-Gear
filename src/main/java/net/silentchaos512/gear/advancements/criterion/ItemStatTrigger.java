package net.silentchaos512.gear.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;

import java.util.Optional;

public class ItemStatTrigger extends SimpleCriterionTrigger<ItemStatTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStat stat, double value) {
        this.trigger(player, instance -> instance.matches(stat, value));
    }

    public record Instance(
            Optional<ContextAwarePredicate> player,
            ItemStat stat,
            MinMaxBounds.Doubles value
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
                        ItemStats.BY_NAME_CODEC.fieldOf("stat").forGetter(Instance::stat),
                        MinMaxBounds.Doubles.CODEC.fieldOf("value").forGetter(Instance::value)
                ).apply(instance, Instance::new)
        );

        public boolean matches(ItemStat statIn, double valueIn) {
            return this.stat == statIn && this.value.matches(valueIn);
        }
    }
}

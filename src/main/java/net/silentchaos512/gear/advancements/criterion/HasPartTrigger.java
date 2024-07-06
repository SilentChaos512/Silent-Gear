package net.silentchaos512.gear.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.GearData;

import java.util.Optional;

public class HasPartTrigger extends SimpleCriterionTrigger<HasPartTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack gear) {
        this.trigger(player, instance -> instance.matches(gear));
    }

    public record Instance(
            Optional<ContextAwarePredicate> player,
            Optional<DataResource<IGearPart>> part,
            Optional<PartType> partType
    ) implements SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                        DataResource.PART_CODEC.optionalFieldOf("part").forGetter(Instance::part),
                        SgRegistries.PART_TYPES.byNameCodec().optionalFieldOf("part_type").forGetter(Instance::partType)
                ).apply(instance, Instance::new)
        );

        public boolean matches(ItemStack gear) {
            return this.part.isPresent() && GearData.hasPart(gear, this.part.get())
                    || this.partType.isPresent() && GearData.hasPartOfType(gear, this.partType.get());
        }
    }
}

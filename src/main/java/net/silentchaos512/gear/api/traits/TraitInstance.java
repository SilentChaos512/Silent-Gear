package net.silentchaos512.gear.api.traits;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

public class TraitInstance implements ITraitInstance {
    private final ITrait trait;
    private final int level;
    private final ImmutableList<ITraitCondition> conditions;

    private TraitInstance(ITrait trait, int level, ITraitCondition... conditions) {
        this.trait = trait;
        this.level = level;
        this.conditions = ImmutableList.<ITraitCondition>builder().add(conditions).build();
    }

    public static TraitInstance of(ITrait trait, int level, ITraitCondition... conditions) {
        return new TraitInstance(trait, level, conditions);
    }

    public static LazyTraitInstance lazy(ResourceLocation traitId, int level, ITraitCondition... conditions) {
        return new LazyTraitInstance(traitId, level, conditions);
    }

    @Override
    public ResourceLocation getTraitId() {
        return trait.getId();
    }

    @Nonnull
    @Override
    public ITrait getTrait() {
        return trait;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Collection<ITraitCondition> getConditions() {
        return conditions;
    }
}

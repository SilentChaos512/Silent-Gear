package net.silentchaos512.gear.api.traits;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.util.DataResource;

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

    /**
     * Gets a trait instance. Will return a {@link TraitInstance} if the trait is loaded, or a
     * {@link LazyTraitInstance} if not.
     *
     * @param trait      The trait
     * @param level      The trait level
     * @param conditions Optional trait conditions
     * @return Trait instance
     */
    public static ITraitInstance of(DataResource<ITrait> trait, int level, ITraitCondition... conditions) {
        if (trait.isPresent()) {
            return of(trait.get(), level, conditions);
        }
        return lazy(trait.getId(), level, conditions);
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

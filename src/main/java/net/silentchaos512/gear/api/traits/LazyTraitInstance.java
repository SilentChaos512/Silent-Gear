package net.silentchaos512.gear.api.traits;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.gear.trait.TraitManager;

import javax.annotation.Nullable;

public class LazyTraitInstance implements ITraitInstance {
    private final ResourceLocation traitId;
    private final int level;
    private final ImmutableList<ITraitCondition> conditions;

    LazyTraitInstance(ResourceLocation traitId, int level, ITraitCondition... conditions) {
        this.traitId = traitId;
        this.level = level;
        this.conditions = ImmutableList.<ITraitCondition>builder().add(conditions).build();
    }

    @Override
    public ResourceLocation getTraitId() {
        return traitId;
    }

    @Nullable
    @Override
    public ITrait getTrait() {
        return TraitManager.get(traitId);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public ImmutableList<ITraitCondition> getConditions() {
        return conditions;
    }
}

package net.silentchaos512.gear.block.trees;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.silentchaos512.gear.world.ModWorldFeatures;

import javax.annotation.Nullable;
import java.util.Random;

public class NetherwoodTree extends AbstractTreeGrower {
    @Nullable
    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204307_, boolean p_204308_) {
        return ModWorldFeatures.Configured.NETHERWOOD_TREE;
    }
}

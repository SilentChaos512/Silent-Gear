package net.silentchaos512.gear.block.trees;

import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.silentchaos512.gear.world.ModWorldFeatures;

import javax.annotation.Nullable;
import java.util.Random;

public class NetherwoodTree extends AbstractTreeGrower {
    @Nullable
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random randomIn, boolean p_225546_2_) {
        return Feature.TREE.configured(ModWorldFeatures.NETHERWOOD_TREE_CONFIG.get());
    }
}

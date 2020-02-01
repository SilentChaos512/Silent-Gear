package net.silentchaos512.gear.block.trees;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.silentchaos512.gear.world.ModWorldFeatures;

import javax.annotation.Nullable;
import java.util.Random;

public class NetherwoodTree extends Tree {
    @Nullable
    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean p_225546_2_) {
        return Feature.ACACIA_TREE.withConfiguration(ModWorldFeatures.NETHERWOOD_TREE_CONFIG);
    }
}

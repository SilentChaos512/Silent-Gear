package net.silentchaos512.gear.block.trees;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.silentchaos512.gear.world.feature.NetherwoodTreeFeature;

import javax.annotation.Nullable;
import java.util.Random;

public class NetherwoodTree extends Tree {
    @Nullable
    @Override
    protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random) {
        return new NetherwoodTreeFeature(true);
    }
}

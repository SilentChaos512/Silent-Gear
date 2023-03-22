package net.silentchaos512.gear.block.trees;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.silentchaos512.gear.init.SgBlocks;
import org.jetbrains.annotations.Nullable;

public class NetherwoodTree extends AbstractTreeGrower {
    public static final TreeConfiguration NETHERWOOD_TREE_CONFIG = new TreeConfiguration.TreeConfigurationBuilder(
            BlockStateProvider.simple(SgBlocks.NETHERWOOD_LOG.asBlockState()),
            new ForkingTrunkPlacer(5, 2, 2),
            BlockStateProvider.simple(SgBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
            new TwoLayersFeatureSize(1, 0, 2))
            .ignoreVines()
            .build();
    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> NETHERWOOD_TREE = Holder.direct(new ConfiguredFeature<>(Feature.TREE, NETHERWOOD_TREE_CONFIG));
    public static final ResourceKey<ConfiguredFeature<?, ?>> KEY = FeatureUtils.createKey("silentgear:netherwood");

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_) {
        return KEY;
    }
}

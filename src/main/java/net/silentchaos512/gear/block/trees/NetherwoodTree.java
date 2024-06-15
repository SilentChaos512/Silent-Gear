package net.silentchaos512.gear.block.trees;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.silentchaos512.gear.setup.SgBlocks;

import java.util.Optional;

public class NetherwoodTree {
    public static final ResourceKey<ConfiguredFeature<?, ?>> KEY = FeatureUtils.createKey("silentgear:netherwood");

    public static final TreeConfiguration TREE_CONFIGURATION = new TreeConfiguration.TreeConfigurationBuilder(
            BlockStateProvider.simple(SgBlocks.NETHERWOOD_LOG.get()),
            new ForkingTrunkPlacer(5, 2, 2),
            BlockStateProvider.simple(SgBlocks.NETHERWOOD_LEAVES.get()),
            new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
            new TwoLayersFeatureSize(1, 0, 2))
            .ignoreVines()
            .build();

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> TREE_CONFIGURATION_HOLDER = Holder.direct(new ConfiguredFeature<>(Feature.TREE, TREE_CONFIGURATION));

    public static final TreeGrower GROWER = new TreeGrower("netherwood", Optional.empty(), Optional.of(KEY), Optional.empty());
}

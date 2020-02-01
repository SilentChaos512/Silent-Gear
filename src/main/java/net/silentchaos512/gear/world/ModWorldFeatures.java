package net.silentchaos512.gear.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModPlacement;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtraConfig;
import net.silentchaos512.lib.world.feature.PlantFeature;

public final class ModWorldFeatures {
    public static final TreeFeatureConfig NETHERWOOD_TREE_CONFIG = (new TreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LOG.asBlockState()),
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new AcaciaFoliagePlacer(2, 0))
            .baseHeight(5)
            .heightRandA(2)
            .heightRandB(2)
            .trunkHeight(0)
            .ignoreVines()
            .setSapling((IPlantable) ModBlocks.NETHERWOOD_SAPLING.asBlock())
            .build());

    private ModWorldFeatures() {}

    public static void addFeaturesToBiomes() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome.getCategory() == Biome.Category.EXTREME_HILLS || biome.getCategory() == Biome.Category.PLAINS) {
                addWildFlax(biome);
            }

            if (biome.getCategory() == Biome.Category.NETHER) {
                addNetherwoodTrees(biome);
                addCrimsonIronOre(biome);
            }
        }
    }

    private static void addWildFlax(Biome biome) {
        SilentGear.LOGGER.info("Add wild flax to {}", biome.getRegistryName());
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                (new PlantFeature(((FlaxPlant) ModBlocks.WILD_FLAX_PLANT.asBlock()).getMaturePlant(), 32, 4))
                        .withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
                        .func_227228_a_(Placement.COUNT_HEIGHTMAP_32.func_227446_a_(new FrequencyConfig(1)))
        );
    }

    private static void addNetherwoodTrees(Biome biome) {
        SilentGear.LOGGER.info("Add netherwood trees to {}", biome.getRegistryName());
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.ACACIA_TREE
                .withConfiguration(NETHERWOOD_TREE_CONFIG)
                .func_227228_a_(ModPlacement.NETHER_FLOOR_WITH_EXTRA
                        .func_227446_a_(new NetherFloorWithExtraConfig(1, 0.25f, 11, 32, 96)))
        );
    }

    private static void addCrimsonIronOre(Biome biome) {
        SilentGear.LOGGER.info("Add crimson iron ore to {}", biome.getRegistryName());
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 6))
                .func_227228_a_(Placement.COUNT_RANGE.func_227446_a_(new CountRangeConfig(24, 24, 0, 120)))
        );
    }
}

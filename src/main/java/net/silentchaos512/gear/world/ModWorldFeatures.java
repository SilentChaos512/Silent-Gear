package net.silentchaos512.gear.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ChanceRangeConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.world.feature.NetherwoodTreeFeature;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtra;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtraConfig;
import net.silentchaos512.lib.world.feature.PlantFeature;

public final class ModWorldFeatures {
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
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(
                new PlantFeature(((FlaxPlant) ModBlocks.FLAX_PLANT.asBlock()).getMaturePlant(), 32, 4),
                IFeatureConfig.NO_FEATURE_CONFIG,
                NetherFloorWithExtra.INSTANCE,
                new NetherFloorWithExtraConfig(0, 0.25f, 6, 32, 96)
        ));
    }

    private static void addNetherwoodTrees(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(
                new NetherwoodTreeFeature(true),
                IFeatureConfig.NO_FEATURE_CONFIG,
                Placement.CHANCE_RANGE,
                new ChanceRangeConfig(0.5f, 32, 32, 128)
        ));
    }

    private static void addCrimsonIronOre(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(
                Feature.ORE,
                new OreFeatureConfig(
                        OreFeatureConfig.FillerBlockType.NETHERRACK,
                        ModBlocks.CRIMSON_IRON_ORE.asBlockState(),
                        6
                ),
                Placement.COUNT_RANGE,
                new CountRangeConfig(24, 24, 0, 120)
        ));
    }
}

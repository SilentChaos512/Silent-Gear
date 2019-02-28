package net.silentchaos512.gear.world;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MinableConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.world.feature.BlueFlowerFeature;
import net.silentchaos512.gear.world.feature.NetherwoodTreeFeature;
import net.silentchaos512.utils.MathUtils;

public final class ModWorldFeatures {
    private ModWorldFeatures() {}

    public static void addFeaturesToBiomes() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (MathUtils.inRangeInclusive(biome.getDefaultTemperature(), 0.5f, 1.5f)) {
                addFlowers(biome);
            }

            if (biome.getCategory() == Biome.Category.NETHER) {
                addNetherwoodTrees(biome);
                addCrimsonIronOre(biome);
            }
        }
    }

    private static void addFlowers(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFlowerFeature(
                new BlueFlowerFeature(),
                Biome.SURFACE_PLUS_32,
                new FrequencyConfig(1)
        ));
    }

    private static void addNetherwoodTrees(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(
                new NetherwoodTreeFeature(true),
                IFeatureConfig.NO_FEATURE_CONFIG,
                Biome.AT_SURFACE_WITH_EXTRA,
                new AtSurfaceWithExtraConfig(0, 0.15f, 6)
        ));
    }

    private static void addCrimsonIronOre(Biome biome) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(
                Feature.MINABLE,
                new MinableConfig(
                        state -> state.getBlock() == Blocks.NETHERRACK,
                        ModBlocks.CRIMSON_IRON_ORE.asBlockState(),
                        6
                ),
                Biome.COUNT_RANGE,
                new CountRangeConfig(24, 24, 0, 120)
        ));
    }
}

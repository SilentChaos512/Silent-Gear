package net.silentchaos512.gear.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.ForkyTrunkPlacer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtra;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtraConfig;
import net.silentchaos512.lib.world.feature.PlantFeature;

public final class ModWorldFeatures {
    public static final Placement<NetherFloorWithExtraConfig> NETHER_FLOOR_WITH_EXTRA = new NetherFloorWithExtra(NetherFloorWithExtraConfig.CODEC);

    public static final BaseTreeFeatureConfig NETHERWOOD_TREE_CONFIG = (new BaseTreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LOG.asBlockState()),
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new AcaciaFoliagePlacer(2, 0, 0, 0),
            new ForkyTrunkPlacer(5, 2, 2),
            new TwoLayerFeature(1, 0, 2))
            .func_236700_a_()
            .build());

    private ModWorldFeatures() {}

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
    }

    public static void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
        event.getRegistry().register(NETHER_FLOOR_WITH_EXTRA.setRegistryName(SilentGear.getId("nether_floor_with_extra")));
    }

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
                (new PlantFeature(ModBlocks.WILD_FLAX_PLANT.asBlockState(), 32, 4))
                        .withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
                        .withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(1)))
        );
    }

    private static void addNetherwoodTrees(Biome biome) {
        SilentGear.LOGGER.info("Add netherwood trees to {}", biome.getRegistryName());
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR
                .withConfiguration(new MultipleRandomFeatureConfig(
                        ImmutableList.of(
                                Feature.field_236291_c_
                                        .withConfiguration(NETHERWOOD_TREE_CONFIG)
                                        .withChance(0.8F)
                        ),
                        Feature.field_236291_c_
                                .withConfiguration(NETHERWOOD_TREE_CONFIG)
                ))
                .withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP
                        .configure(new NetherFloorWithExtraConfig(1, 0.25f, 11, 32, 96))));
    }

    private static void addCrimsonIronOre(Biome biome) {
        SilentGear.LOGGER.info("Add crimson iron ore to {}", biome.getRegistryName());
        // FIXME: There are biomes with less netherrack now, right? Might need to tweak vein counts for those.
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 6))
                .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(24, 24, 0, 120)))
        );
    }
}

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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.world.feature.NetherwoodTreeFeature;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtra;
import net.silentchaos512.gear.world.placement.NetherFloorWithExtraConfig;
import net.silentchaos512.lib.world.feature.PlantFeature;

public final class ModWorldFeatures {
    public static final NetherwoodTreeFeature NETHERWOOD_TREE_FEATURE = new NetherwoodTreeFeature(TreeFeatureConfig::deserializeAcacia);

    public static final Placement<NetherFloorWithExtraConfig> NETHER_FLOOR_WITH_EXTRA = new NetherFloorWithExtra(NetherFloorWithExtraConfig::deserialize);

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

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(NETHERWOOD_TREE_FEATURE.setRegistryName(SilentGear.getId("netherwood_tree")));
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
                (new PlantFeature(((FlaxPlant) ModBlocks.WILD_FLAX_PLANT.asBlock()).getMaturePlant(), 32, 4))
                        .withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
                        .withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(1)))
        );
    }

    private static void addNetherwoodTrees(Biome biome) {
        SilentGear.LOGGER.info("Add netherwood trees to {}", biome.getRegistryName());
        biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, NETHERWOOD_TREE_FEATURE
                .withConfiguration(NETHERWOOD_TREE_CONFIG)
                .withPlacement(NETHER_FLOOR_WITH_EXTRA
                        .configure(new NetherFloorWithExtraConfig(1, 0.25f, 11, 32, 96)))
        );
    }

    private static void addCrimsonIronOre(Biome biome) {
        SilentGear.LOGGER.info("Add crimson iron ore to {}", biome.getRegistryName());
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 6))
                .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(24, 24, 0, 120)))
        );
    }
}

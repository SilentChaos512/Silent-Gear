package net.silentchaos512.gear.world;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.util.ModResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ModWorldFeatures {
    public static final RuleTest END_STONE_RULE_TEST = new TagMatchTest(Tags.Blocks.END_STONES);

    private static boolean configuredFeaturesRegistered = false;

    @SuppressWarnings("WeakerAccess")
    public static final class Configured {
        static final Map<String, Lazy<ConfiguredFeature<?, ?>>> TO_REGISTER = new LinkedHashMap<>();

        private static final RandomPatchConfiguration WILD_FLAX_PATCHES_CONFIG = FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.WILD_FLAX_PLANT.get())), List.of(), 32);
        private static final RandomPatchConfiguration WILD_FLUFFY_PATCHES_CONFIG = FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.WILD_FLUFFY_PLANT.get())), List.of(), 32);
        public static final TreeConfiguration NETHERWOOD_TREE_CONFIG = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.NETHERWOOD_LOG.asBlockState()),
                new ForkingTrunkPlacer(5, 2, 2),
                BlockStateProvider.simple(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
                new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 2))
                .ignoreVines()
                .build();

        public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> WILD_FLAX_PATCHES = create("wild_flax_patches", Feature.FLOWER, WILD_FLAX_PATCHES_CONFIG);
        public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> WILD_FLUFFY_PATCHES = create("wild_fluffy_patches", Feature.FLOWER, WILD_FLUFFY_PATCHES_CONFIG);
        public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> NETHERWOOD_TREE = create("netherwood_tree", Feature.TREE, NETHERWOOD_TREE_CONFIG);

        /*public static final Lazy<ConfiguredFeature<?, ?>> NETHERWOOD_TREES = createLazy("netherwood_trees", () -> Feature.RANDOM_SELECTOR
                .configured(new RandomFeatureConfiguration(
                        ImmutableList.of(
                                NETHERWOOD_TREE_FEATURE.get()
                                        .configured(NETHERWOOD_TREE_CONFIG.get())
                                        .weighted(0.8F)
                        ),
                        NETHERWOOD_TREE_FEATURE.get()
                                .configured(NETHERWOOD_TREE_CONFIG.get())
                ))
                .decorated(FeatureDecorator.COUNT_MULTILAYER.configured(new CountConfiguration(8)))
                .range(128)
                .chance(2));*/

        public static <FC extends FeatureConfiguration> Holder<ConfiguredFeature<FC, ?>> create(String name, Feature<FC> feature, FC featureConfig) {
            return FeatureUtils.register("silentgear:" + name, feature, featureConfig);
        }

        private static Lazy<ConfiguredFeature<?, ?>> createLazy(String name, Supplier<ConfiguredFeature<?, ?>> supplier) {
            if (TO_REGISTER.containsKey(name)) {
                throw new IllegalArgumentException("Configured feature lazy with name '" + name + "' already created");
            }

            Lazy<ConfiguredFeature<?, ?>> lazy = Lazy.of(supplier);
            TO_REGISTER.put(name, lazy);
            return lazy;
        }

        private Configured() {}
    }

    public static final class Placed {
        public static final Holder<PlacedFeature> FLOWER_WILD_FLAX = create("flower_wild_flax", Configured.WILD_FLAX_PATCHES,
                RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());

        public static final Holder<PlacedFeature> FLOWER_WILD_FLUFFY = create("flower_wild_fluffy", Configured.WILD_FLUFFY_PATCHES,
                RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());

        private static <FC extends FeatureConfiguration> Holder<PlacedFeature> create(String name, Holder<ConfiguredFeature<FC, ?>> configuredFeature, List<PlacementModifier> modifiers) {
            return PlacementUtils.register("silentgear:" + name, configuredFeature, modifiers);
        }

        private static <FC extends FeatureConfiguration> Holder<PlacedFeature> create(String name, Holder<ConfiguredFeature<FC, ?>> configuredFeature, PlacementModifier... modifiers) {
            return PlacementUtils.register("silentgear:" + name, configuredFeature, modifiers);
        }

        private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
            return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
        }

        private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
            return orePlacement(CountPlacement.of(p_195344_), p_195345_);
        }

        private static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
            return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
        }

        private Placed() {}
    }

    private ModWorldFeatures() {}

//    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
////        event.getRegistry().register(NETHERWOOD_TREE_FEATURE.get().setRegistryName(SilentGear.getId("netherwood_tree")));
//    }

    private static void registerConfiguredFeatures() {
        if (configuredFeaturesRegistered) return;

        configuredFeaturesRegistered = true;

        Configured.TO_REGISTER.forEach((name, cf) -> registerConfiguredFeature(name, cf.get()));
    }

    private static void registerConfiguredFeature(String name, ConfiguredFeature<?, ?> configuredFeature) {
        ModResourceLocation id = SilentGear.getId(name);
        debugLog("Register configured feature " + id);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
    }

    /*@SubscribeEvent
    public static void addFeaturesToBiomes(BiomeLoadingEvent biome) {
        // Need to load these as late as possible, or configs won't be loaded
        registerConfiguredFeatures();

        if (biome.getCategory() == Biome.BiomeCategory.NETHER) {
            addNetherwoodTrees(biome);
            addCrimsonIronOre(biome);
        } else if (biome.getCategory() == Biome.BiomeCategory.THEEND) {
            addAzureSilverOre(biome);
        } else {
            addBortOre(biome);

            if (biome.getCategory() == Biome.BiomeCategory.EXTREME_HILLS || biome.getCategory() == Biome.BiomeCategory.PLAINS) {
                addWildFlax(biome);
            }
            if (biome.getClimate().downfall > 0.4f) {
                addWildFluffyPlants(biome);
            }
        }
    }

    private static void addWildFlax(BiomeLoadingEvent biome) {
        debugLog("Add wild flax to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placed.FLOWER_WILD_FLAX);
    }

    private static void addWildFluffyPlants(BiomeLoadingEvent biome) {
        debugLog("Add wild fluffy plants to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placed.FLOWER_WILD_FLUFFY);
    }

    private static void addNetherwoodTrees(BiomeLoadingEvent biome) {
//        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Configured.NETHERWOOD_TREES);
    }

    private static void addBortOre(BiomeLoadingEvent biome) {
        biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_BORT);
    }

    private static void addCrimsonIronOre(BiomeLoadingEvent biome) {
        if (Biomes.BASALT_DELTAS.location().equals(biome.getName()) || Biomes.SOUL_SAND_VALLEY.location().equals(biome.getName())) {
            debugLog("Add double crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_CRIMSON_IRON_DOUBLE);
        } else {
            debugLog("Add crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_CRIMSON_IRON);
        }
    }

    private static void addAzureSilverOre(BiomeLoadingEvent biome) {
        debugLog("Add azure silver ores to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_AZURE_SILVER);
    }*/

    private static void debugLog(String msg) {
        if (Config.Common.worldGenLogging.get()) {
            SilentGear.LOGGER.debug(msg);
        }
    }
}

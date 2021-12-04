package net.silentchaos512.gear.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    public static final Lazy<TreeConfiguration> NETHERWOOD_TREE_CONFIG = Lazy.of(() -> new TreeConfiguration.TreeConfigurationBuilder(
            BlockStateProvider.simple(ModBlocks.NETHERWOOD_LOG.asBlockState()),
            new ForkingTrunkPlacer(5, 2, 2),
            BlockStateProvider.simple(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
            new TwoLayersFeatureSize(1, 0, 2))
            .ignoreVines()
            .build());

//    public static final Lazy<NetherwoodTreeFeature> NETHERWOOD_TREE_FEATURE = Lazy.of(() -> new NetherwoodTreeFeature(TreeConfiguration.CODEC));

    private static boolean configuredFeaturesRegistered = false;

    @SuppressWarnings("WeakerAccess")
    public static final class Configured {
        static final Map<String, Lazy<ConfiguredFeature<?, ?>>> TO_REGISTER = new LinkedHashMap<>();

        public static final Lazy<ConfiguredFeature<?, ?>> BORT_ORE_VEINS = createLazy("bort_ore_veins", () -> {
            ImmutableList<OreConfiguration.TargetBlockState> targetList = ImmutableList.of(
                    OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.BORT_ORE.get().defaultBlockState()),
                    OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_BORT_ORE.get().defaultBlockState()));
            return Feature.REPLACE_SINGLE_BLOCK.configured(new ReplaceBlockConfiguration(targetList));
        });

        public static final Lazy<ConfiguredFeature<?, ?>> CRIMSON_IRON_ORE_VEINS = createLazy("crimson_iron_ore_veins", () -> Feature.ORE
                .configured(new OreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8)));

        public static final Lazy<ConfiguredFeature<?, ?>> AZURE_SILVER_ORE_VEINS = createLazy("azure_silver_ore_veins", () -> Feature.ORE
                .configured(new OreConfiguration(END_STONE_RULE_TEST, ModBlocks.AZURE_SILVER_ORE.asBlockState(), 6)));

        public static final Lazy<ConfiguredFeature<?, ?>> WILD_FLAX_PATCHES = createLazy("wild_flax_patches", () -> Feature.FLOWER
                .configured(grassPatch(BlockStateProvider.simple(ModBlocks.WILD_FLAX_PLANT.get()), Config.Common.wildFlaxPatchCount.get())));

        public static final Lazy<ConfiguredFeature<?, ?>> WILD_FLUFFY_PATCHES = createLazy("wild_fluffy_plant_patches", () -> Feature.FLOWER
                .configured(grassPatch(BlockStateProvider.simple(ModBlocks.WILD_FLUFFY_PLANT.get()), Config.Common.wildFluffyPatchCount.get())));

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

        private static Lazy<ConfiguredFeature<?, ?>> createLazy(String name, Supplier<ConfiguredFeature<?, ?>> supplier) {
            if (TO_REGISTER.containsKey(name)) {
                throw new IllegalArgumentException("Configured feature lazy with name '" + name + "' already created");
            }

            Lazy<ConfiguredFeature<?, ?>> lazy = Lazy.of(supplier);
            TO_REGISTER.put(name, lazy);
            return lazy;
        }

        private static RandomPatchConfiguration grassPatch(BlockStateProvider p_195203_, int p_195204_) {
            return FeatureUtils.simpleRandomPatchConfiguration(p_195204_, Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(p_195203_)).onlyWhenEmpty());
        }

        private Configured() {}
    }

    public static final class Placed {
        public static final Lazy<PlacedFeature> ORE_BORT = create("ore_bort", () -> Configured.BORT_ORE_VEINS.get()
                .placed(commonOrePlacement(Config.Common.bortCount.get(),
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-60), VerticalAnchor.absolute(10)))));

        public static final Lazy<PlacedFeature> ORE_CRIMSON_IRON = create("ore_crimson_iron", () -> Configured.CRIMSON_IRON_ORE_VEINS.get()
                .placed(commonOrePlacement(Config.Common.crimsonIronCount.get(),
                        PlacementUtils.RANGE_10_10)));

        public static final Lazy<PlacedFeature> ORE_CRIMSON_IRON_DOUBLE = create("ore_crimson_iron_double", () -> Configured.CRIMSON_IRON_ORE_VEINS.get()
                .placed(commonOrePlacement(2 * Config.Common.crimsonIronCount.get(),
                        PlacementUtils.RANGE_10_10)));

        public static final Lazy<PlacedFeature> ORE_AZURE_SILVER = create("ore_azure_silver", () -> Configured.AZURE_SILVER_ORE_VEINS.get()
        .placed(commonOrePlacement(Config.Common.azureSilverCount.get(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(92)))));

        public static final Lazy<PlacedFeature> FLOWER_WILD_FLAX = create("flower_wild_flax", () -> Configured.WILD_FLAX_PATCHES.get()
        .placed(RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

        public static final Lazy<PlacedFeature> FLOWER_WILD_FLUFFY = create("flower_wild_fluffy", () -> Configured.WILD_FLUFFY_PATCHES.get()
                .placed(RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

        private static Lazy<PlacedFeature> create(String name, Supplier<PlacedFeature> supplier) {
            return Lazy.of(supplier);
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

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
//        event.getRegistry().register(NETHERWOOD_TREE_FEATURE.get().setRegistryName(SilentGear.getId("netherwood_tree")));
    }

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

    @SubscribeEvent
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
        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placed.FLOWER_WILD_FLAX.get());
    }

    private static void addWildFluffyPlants(BiomeLoadingEvent biome) {
        debugLog("Add wild fluffy plants to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Placed.FLOWER_WILD_FLUFFY.get());
    }

    private static void addNetherwoodTrees(BiomeLoadingEvent biome) {
//        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Configured.NETHERWOOD_TREES.get());
    }

    private static void addBortOre(BiomeLoadingEvent biome) {
        biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_BORT.get());
    }

    private static void addCrimsonIronOre(BiomeLoadingEvent biome) {
        if (Biomes.BASALT_DELTAS.location().equals(biome.getName()) || Biomes.SOUL_SAND_VALLEY.location().equals(biome.getName())) {
            debugLog("Add double crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_CRIMSON_IRON_DOUBLE.get());
        } else {
            debugLog("Add crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_CRIMSON_IRON.get());
        }
    }

    private static void addAzureSilverOre(BiomeLoadingEvent biome) {
        debugLog("Add azure silver ores to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Placed.ORE_AZURE_SILVER.get());
    }

    private static void debugLog(String msg) {
        if (Config.Common.worldGenLogging.get()) {
            SilentGear.LOGGER.debug(msg);
        }
    }
}

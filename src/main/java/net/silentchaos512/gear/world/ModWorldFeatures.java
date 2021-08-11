package net.silentchaos512.gear.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Features;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
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
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ModWorldFeatures {
    public static final RuleTest END_STONE_RULE_TEST = new TagMatchTest(Tags.Blocks.END_STONES);

    public static final Lazy<TreeConfiguration> NETHERWOOD_TREE_CONFIG = Lazy.of(() -> new TreeConfiguration.TreeConfigurationBuilder(
            new SimpleStateProvider(ModBlocks.NETHERWOOD_LOG.asBlockState()),
            new ForkingTrunkPlacer(5, 2, 2),
            new SimpleStateProvider(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new SimpleStateProvider(ModBlocks.NETHERWOOD_SAPLING.asBlockState()),
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
                    OreConfiguration.target(OreConfiguration.Predicates.STONE_ORE_REPLACEABLES, ModBlocks.BORT_ORE.get().defaultBlockState()),
                    OreConfiguration.target(OreConfiguration.Predicates.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_BORT_ORE.get().defaultBlockState()));
            return Feature.REPLACE_SINGLE_BLOCK
                    .configured(new ReplaceBlockConfiguration(targetList))
                    .rangeUniform(VerticalAnchor.aboveBottom(4), VerticalAnchor.absolute(20))
                    .squared()
                    .count(Config.Common.bortCount.get());
        });

        public static final Lazy<ConfiguredFeature<?, ?>> CRIMSON_IRON_ORE_VEINS = createLazy("crimson_iron_ore_veins", () -> Feature.ORE
                .configured(new OreConfiguration(OreConfiguration.Predicates.NETHER_ORE_REPLACEABLES, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .rangeTriangle(VerticalAnchor.aboveBottom(24), VerticalAnchor.belowTop(8))
                .squared()
                .count(Config.Common.crimsonIronCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> DOUBLE_CRIMSON_IRON_ORE_VEINS = createLazy("double_crimson_iron_ore_veins", () -> Feature.ORE
                .configured(new OreConfiguration(OreConfiguration.Predicates.NETHER_ORE_REPLACEABLES, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .rangeTriangle(VerticalAnchor.aboveBottom(24), VerticalAnchor.belowTop(8))
                .squared()
                .count(2 * Config.Common.crimsonIronCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> AZURE_SILVER_ORE_VEINS = createLazy("azure_silver_ore_veins", () -> Feature.ORE
                .configured(new OreConfiguration(END_STONE_RULE_TEST, ModBlocks.AZURE_SILVER_ORE.asBlockState(), 6))
                .rangeUniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(92))
                .squared()
                .count(Config.Common.azureSilverCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> WILD_FLAX_PATCHES = createLazy("wild_flax_patches", () -> Feature.FLOWER
                .configured(new RandomPatchConfiguration.GrassConfigurationBuilder(
                        new SimpleStateProvider(ModBlocks.WILD_FLAX_PLANT.asBlockState()),
                        SimpleBlockPlacer.INSTANCE
                ).tries(Config.Common.wildFlaxTryCount.get()).build())
                .decorated(Features.Decorators.ADD_32)
                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
                .count(Config.Common.wildFlaxPatchCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> WILD_FLUFFY_PATCHES = createLazy("wild_fluffy_plant_patches", () -> Feature.FLOWER
                .configured(new RandomPatchConfiguration.GrassConfigurationBuilder(
                        new SimpleStateProvider(ModBlocks.WILD_FLUFFY_PLANT.asBlockState()),
                        SimpleBlockPlacer.INSTANCE
                ).tries(Config.Common.wildFluffyTryCount.get()).build())
                .decorated(Features.Decorators.ADD_32)
                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
                .count(Config.Common.wildFluffyPatchCount.get()));

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

        private Configured() {}
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

    public static void registerPlacements(RegistryEvent.Register<FeatureDecorator<?>> event) {
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
        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Configured.WILD_FLAX_PATCHES.get());
    }

    private static void addWildFluffyPlants(BiomeLoadingEvent biome) {
        debugLog("Add wild fluffy plants to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Configured.WILD_FLUFFY_PATCHES.get());
    }

    private static void addNetherwoodTrees(BiomeLoadingEvent biome) {
//        biome.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Configured.NETHERWOOD_TREES.get());
    }

    private static void addBortOre(BiomeLoadingEvent biome) {
        biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Configured.BORT_ORE_VEINS.get());
    }

    private static void addCrimsonIronOre(BiomeLoadingEvent biome) {
        if (Biomes.BASALT_DELTAS.location().equals(biome.getName()) || Biomes.SOUL_SAND_VALLEY.location().equals(biome.getName())) {
            debugLog("Add double crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Configured.DOUBLE_CRIMSON_IRON_ORE_VEINS.get());
        } else {
            debugLog("Add crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Configured.CRIMSON_IRON_ORE_VEINS.get());
        }
    }

    private static void addAzureSilverOre(BiomeLoadingEvent biome) {
        debugLog("Add azure silver ores to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Configured.AZURE_SILVER_ORE_VEINS.get());
    }

    private static void debugLog(String msg) {
        if (Config.Common.worldGenLogging.get()) {
            SilentGear.LOGGER.debug(msg);
        }
    }
}

package net.silentchaos512.gear.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.trunkplacer.ForkyTrunkPlacer;
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
import net.silentchaos512.gear.world.feature.NetherwoodTreeFeature;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ModWorldFeatures {
    public static final RuleTest END_STONE_RULE_TEST = new TagMatchRuleTest(Tags.Blocks.END_STONES);

    public static final Lazy<BaseTreeFeatureConfig> NETHERWOOD_TREE_CONFIG = Lazy.of(() -> new BaseTreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LOG.asBlockState()),
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new AcaciaFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0)),
            new ForkyTrunkPlacer(5, 2, 2),
            new TwoLayerFeature(1, 0, 2))
            .ignoreVines()
            .build());

    public static final Lazy<NetherwoodTreeFeature> NETHERWOOD_TREE_FEATURE = Lazy.of(() -> new NetherwoodTreeFeature(BaseTreeFeatureConfig.CODEC));

    private static boolean configuredFeaturesRegistered = false;

    @SuppressWarnings("WeakerAccess")
    public static final class Configured {
        static final Map<String, Lazy<ConfiguredFeature<?, ?>>> TO_REGISTER = new LinkedHashMap<>();

        public static final Lazy<ConfiguredFeature<?, ?>> BORT_ORE_VEINS = createLazy("bort_ore_veins", () -> Feature.EMERALD_ORE
                .configured(new ReplaceBlockConfig(Blocks.STONE.defaultBlockState(), ModBlocks.BORT_ORE.asBlockState()))
                .decorated(Placement.RANGE.configured(topSolidRange(4, 20)))
                .squared()
                .count(Config.Common.bortCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> CRIMSON_IRON_ORE_VEINS = createLazy("crimson_iron_ore_veins", () -> Feature.ORE
                .configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .decorated(Placement.RANGE.configured(topSolidRange(24, 120)))
                .squared()
                .count(Config.Common.crimsonIronCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> DOUBLE_CRIMSON_IRON_ORE_VEINS = createLazy("double_crimson_iron_ore_veins", () -> Feature.ORE
                .configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .decorated(Placement.RANGE.configured(topSolidRange(24, 120)))
                .squared()
                .count(2 * Config.Common.crimsonIronCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> AZURE_SILVER_ORE_VEINS = createLazy("azure_silver_ore_veins", () -> Feature.ORE
                .configured(new OreFeatureConfig(END_STONE_RULE_TEST, ModBlocks.AZURE_SILVER_ORE.asBlockState(), 6))
                .decorated(Placement.RANGE.configured(topSolidRange(16, 92)))
                .squared()
                .count(Config.Common.azureSilverCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> WILD_FLAX_PATCHES = createLazy("wild_flax_patches", () -> Feature.FLOWER
                .configured(new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(ModBlocks.WILD_FLAX_PLANT.asBlockState()),
                        SimpleBlockPlacer.INSTANCE
                ).tries(Config.Common.wildFlaxTryCount.get()).build())
                .decorated(Features.Placements.ADD_32)
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .count(Config.Common.wildFlaxPatchCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> WILD_FLUFFY_PATCHES = createLazy("wild_fluffy_plant_patches", () -> Feature.FLOWER
                .configured(new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(ModBlocks.WILD_FLUFFY_PLANT.asBlockState()),
                        SimpleBlockPlacer.INSTANCE
                ).tries(Config.Common.wildFluffyTryCount.get()).build())
                .decorated(Features.Placements.ADD_32)
                .decorated(Features.Placements.HEIGHTMAP_SQUARE)
                .count(Config.Common.wildFluffyPatchCount.get()));

        public static final Lazy<ConfiguredFeature<?, ?>> NETHERWOOD_TREES = createLazy("netherwood_trees", () -> Feature.RANDOM_SELECTOR
                .configured(new MultipleRandomFeatureConfig(
                        ImmutableList.of(
                                NETHERWOOD_TREE_FEATURE.get()
                                        .configured(NETHERWOOD_TREE_CONFIG.get())
                                        .weighted(0.8F)
                        ),
                        NETHERWOOD_TREE_FEATURE.get()
                                .configured(NETHERWOOD_TREE_CONFIG.get())
                ))
                .decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(8)))
                .range(128)
                .chance(2));

        private static Lazy<ConfiguredFeature<?, ?>> createLazy(String name, Supplier<ConfiguredFeature<?, ?>> supplier) {
            if (TO_REGISTER.containsKey(name)) {
                throw new IllegalArgumentException("Configured feature lazy with name '" + name + "' already created");
            }

            Lazy<ConfiguredFeature<?, ?>> lazy = Lazy.of(supplier);
            TO_REGISTER.put(name, lazy);
            return lazy;
        }

        @Nonnull
        private static TopSolidRangeConfig topSolidRange(int bottom, int top) {
            return new TopSolidRangeConfig(bottom, 0, top - bottom);
        }

        private Configured() {}
    }

    private ModWorldFeatures() {}

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(NETHERWOOD_TREE_FEATURE.get().setRegistryName(SilentGear.getId("netherwood_tree")));
    }

    private static void registerConfiguredFeatures() {
        if (configuredFeaturesRegistered) return;

        configuredFeaturesRegistered = true;

        Configured.TO_REGISTER.forEach((name, cf) -> registerConfiguredFeature(name, cf.get()));
    }

    private static void registerConfiguredFeature(String name, ConfiguredFeature<?, ?> configuredFeature) {
        ModResourceLocation id = SilentGear.getId(name);
        debugLog("Register configured feature " + id);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, id, configuredFeature);
    }

    public static void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
    }

    @SubscribeEvent
    public static void addFeaturesToBiomes(BiomeLoadingEvent biome) {
        // Need to load these as late as possible, or configs won't be loaded
        registerConfiguredFeatures();

        if (biome.getCategory() == Biome.Category.NETHER) {
            addNetherwoodTrees(biome);
            addCrimsonIronOre(biome);
        } else if (biome.getCategory() == Biome.Category.THEEND) {
            addAzureSilverOre(biome);
        } else {
            addBortOre(biome);

            if (biome.getCategory() == Biome.Category.EXTREME_HILLS || biome.getCategory() == Biome.Category.PLAINS) {
                addWildFlax(biome);
            }
            if (biome.getClimate().downfall > 0.4f) {
                addWildFluffyPlants(biome);
            }
        }
    }

    private static void addWildFlax(BiomeLoadingEvent biome) {
        debugLog("Add wild flax to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Configured.WILD_FLAX_PATCHES.get());
    }

    private static void addWildFluffyPlants(BiomeLoadingEvent biome) {
        debugLog("Add wild fluffy plants to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Configured.WILD_FLUFFY_PATCHES.get());
    }

    private static void addNetherwoodTrees(BiomeLoadingEvent biome) {
        biome.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Configured.NETHERWOOD_TREES.get());
    }

    private static void addBortOre(BiomeLoadingEvent biome) {
        biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.BORT_ORE_VEINS.get());
    }

    private static void addCrimsonIronOre(BiomeLoadingEvent biome) {
        if (Biomes.BASALT_DELTAS.location().equals(biome.getName()) || Biomes.SOUL_SAND_VALLEY.location().equals(biome.getName())) {
            debugLog("Add double crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.DOUBLE_CRIMSON_IRON_ORE_VEINS.get());
        } else {
            debugLog("Add crimson iron ores to " + biome.getName());
            biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.CRIMSON_IRON_ORE_VEINS.get());
        }
    }

    private static void addAzureSilverOre(BiomeLoadingEvent biome) {
        debugLog("Add azure silver ores to " + biome.getName());
        biome.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.AZURE_SILVER_ORE_VEINS.get());
    }

    private static void debugLog(String msg) {
        if (Config.Common.worldGenLogging.get()) {
            SilentGear.LOGGER.debug(msg);
        }
    }
}

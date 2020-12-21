package net.silentchaos512.gear.world;

import com.google.common.collect.ImmutableList;
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
import net.silentchaos512.gear.world.feature.NetherwoodTreeFeature;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ModWorldFeatures {
    public static final RuleTest END_STONE_RULE_TEST = new TagMatchRuleTest(Tags.Blocks.END_STONES);

    public static final Lazy<BaseTreeFeatureConfig> NETHERWOOD_TREE_CONFIG = Lazy.of(() -> new BaseTreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LOG.asBlockState()),
            new SimpleBlockStateProvider(ModBlocks.NETHERWOOD_LEAVES.asBlockState()),
            new AcaciaFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0)),
            new ForkyTrunkPlacer(5, 2, 2),
            new TwoLayerFeature(1, 0, 2))
            .setIgnoreVines()
            .build());

    public static final Lazy<NetherwoodTreeFeature> NETHERWOOD_TREE_FEATURE = Lazy.of(() -> new NetherwoodTreeFeature(BaseTreeFeatureConfig.CODEC));

    @SuppressWarnings("WeakerAccess")
    public static final class Configured {
        public static final ConfiguredFeature<?, ?> CRIMSON_IRON_ORE_VEINS = Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_NETHER, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(24, 0, 120)))
                .square()
                .func_242731_b(Config.Common.crimsonIronCount.get());

        public static final ConfiguredFeature<?, ?> DOUBLE_CRIMSON_IRON_ORE_VEINS = Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_NETHER, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(24, 0, 120)))
                .square()
                .func_242731_b(2 * Config.Common.crimsonIronCount.get());

        public static final ConfiguredFeature<?, ?> AZURE_SILVER_ORE_VEINS = Feature.ORE
                .withConfiguration(new OreFeatureConfig(END_STONE_RULE_TEST, ModBlocks.AZURE_SILVER_ORE.asBlockState(), 6))
                .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(16, 0, 92)))
                .square()
                .func_242731_b(Config.Common.azureSilverCount.get());

        public static final ConfiguredFeature<?, ?> WILD_FLAX_PATCHES = Feature.FLOWER
                .withConfiguration(new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(ModBlocks.WILD_FLAX_PLANT.asBlockState()),
                        SimpleBlockPlacer.PLACER
                ).tries(Config.Common.wildFlaxTryCount.get()).build())
                .withPlacement(Features.Placements.VEGETATION_PLACEMENT)
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .func_242731_b(Config.Common.wildFlaxPatchCount.get());

        public static final ConfiguredFeature<?, ?> NETHERWOOD_TREES = Feature.RANDOM_SELECTOR
                .withConfiguration(new MultipleRandomFeatureConfig(
                        ImmutableList.of(
                                NETHERWOOD_TREE_FEATURE.get()
                                        .withConfiguration(NETHERWOOD_TREE_CONFIG.get())
                                        .withChance(0.8F)
                        ),
                        NETHERWOOD_TREE_FEATURE.get()
                                .withConfiguration(NETHERWOOD_TREE_CONFIG.get())
                ))
                .withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(8)))
                .range(128)
                .chance(2);

        private Configured() {}
    }

    private ModWorldFeatures() {}

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(NETHERWOOD_TREE_FEATURE.get().setRegistryName(SilentGear.getId("netherwood_tree")));

        registerConfiguredFeature("crimson_iron_ore_veins", Configured.CRIMSON_IRON_ORE_VEINS);
        registerConfiguredFeature("azure_silver_ore_veins", Configured.AZURE_SILVER_ORE_VEINS);
        registerConfiguredFeature("wild_flax_patches", Configured.WILD_FLAX_PATCHES);
        registerConfiguredFeature("netherwood_trees", Configured.NETHERWOOD_TREES);
    }

    private static void registerConfiguredFeature(String name, ConfiguredFeature<?, ?> configuredFeature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, SilentGear.getId(name), configuredFeature);
    }

    public static void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
    }

    @SubscribeEvent
    public static void addFeaturesToBiomes(BiomeLoadingEvent biome) {
        if (biome.getCategory() == Biome.Category.EXTREME_HILLS || biome.getCategory() == Biome.Category.PLAINS) {
            addWildFlax(biome);
        }

        if (biome.getCategory() == Biome.Category.NETHER) {
            addNetherwoodTrees(biome);
            addCrimsonIronOre(biome);
        }

        if (biome.getCategory() == Biome.Category.THEEND) {
            addAzureSilverOre(biome);
        }
    }

    private static void addWildFlax(BiomeLoadingEvent biome) {
        SilentGear.LOGGER.debug("Add wild flax to {}", biome.getName());
        biome.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Configured.WILD_FLAX_PATCHES);
    }

    private static void addNetherwoodTrees(BiomeLoadingEvent biome) {
        biome.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Configured.NETHERWOOD_TREES);
    }

    private static void addCrimsonIronOre(BiomeLoadingEvent biome) {
        if (Biomes.BASALT_DELTAS.getLocation().equals(biome.getName()) || Biomes.SOUL_SAND_VALLEY.getLocation().equals(biome.getName())) {
            SilentGear.LOGGER.debug("Add double crimson iron ores to {}", biome.getName());
            biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.DOUBLE_CRIMSON_IRON_ORE_VEINS);
        } else {
            SilentGear.LOGGER.debug("Add crimson iron ores to {}", biome.getName());
            biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.CRIMSON_IRON_ORE_VEINS);
        }
    }

    private static void addAzureSilverOre(BiomeLoadingEvent biome) {
        biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Configured.AZURE_SILVER_ORE_VEINS);
    }
}

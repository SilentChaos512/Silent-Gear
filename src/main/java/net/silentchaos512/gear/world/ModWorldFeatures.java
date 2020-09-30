package net.silentchaos512.gear.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.biome.Biome;
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

    private ModWorldFeatures() {}

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(NETHERWOOD_TREE_FEATURE.get().setRegistryName(SilentGear.getId("netherwood_tree")));
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
        SilentGear.LOGGER.info("Add wild flax to {}", biome.getName());
        biome.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER
                .withConfiguration(new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(ModBlocks.WILD_FLAX_PLANT.asBlockState()),
                        SimpleBlockPlacer.PLACER
                ).tries(64).build())
                .withPlacement(Features.Placements.VEGETATION_PLACEMENT)
                .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                .func_242731_b(1));
    }

    private static void addNetherwoodTrees(BiomeLoadingEvent biome) {
        SilentGear.LOGGER.info("Add netherwood trees to {}", biome.getName());
        biome.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR
                .withConfiguration(new MultipleRandomFeatureConfig(
                        ImmutableList.of(
                                NETHERWOOD_TREE_FEATURE.get()
                                        .withConfiguration(NETHERWOOD_TREE_CONFIG.get())
                                        .withChance(0.8F)
                        ),
                        NETHERWOOD_TREE_FEATURE.get()
                                .withConfiguration(NETHERWOOD_TREE_CONFIG.get())
                ))
                .withPlacement(Placement.field_242897_C.configure(new FeatureSpreadConfig(8)))
                .func_242733_d(128)
                .func_242729_a(2)
        );
    }

    private static void addCrimsonIronOre(BiomeLoadingEvent biome) {
        SilentGear.LOGGER.info("Add crimson iron ore to {}", biome.getName());
        // FIXME: There are biomes with less netherrack now, right? Might need to tweak vein counts for those.
        biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, ModBlocks.CRIMSON_IRON_ORE.asBlockState(), 8))
                .withPlacement(Placement.field_242907_l.configure(new TopSolidRangeConfig(24, 0, 120)))
                .func_242731_b(24)
        );
    }

    private static void addAzureSilverOre(BiomeLoadingEvent biome) {
        SilentGear.LOGGER.info("Add azure silver ore to {}", biome.getName());
        biome.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE
                .withConfiguration(new OreFeatureConfig(END_STONE_RULE_TEST, ModBlocks.AZURE_SILVER_ORE.asBlockState(), 6))
                .withPlacement(Placement.field_242907_l.configure(new TopSolidRangeConfig(16, 0, 92)))
                .func_242731_b(15)
        );
    }
}

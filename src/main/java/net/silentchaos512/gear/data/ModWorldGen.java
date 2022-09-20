package net.silentchaos512.gear.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

import java.util.List;
import java.util.Map;

public class ModWorldGen {
    public static void init(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());

        ResourceLocation bortName = SilentGear.getId("bort_ore");
        ResourceLocation crimsonIronName = SilentGear.getId("crimson_iron_ore");
        ResourceLocation azureSilverName = SilentGear.getId("azure_silver_ore");

        // Bort
        ConfiguredFeature<?, ?> bortFeature = new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(
                        List.of(
                                OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.BORT_ORE.asBlockState()),
                                OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_BORT_ORE.asBlockState())
                        ),
                        2,
                        0.3f
                )
        );
        PlacedFeature bortPlaced = new PlacedFeature(
                holder(bortFeature, ops, bortName),
                commonOrePlacement(6, HeightRangePlacement.triangle(
                                VerticalAnchor.absolute(-60), VerticalAnchor.absolute(10)
                        )
                )
        );

        // Crimson Iron
        ConfiguredFeature<?, ?> crimsonIronFeature = new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(
                        List.of(
                                OreConfiguration.target(new TagMatchTest(Tags.Blocks.NETHERRACK), ModBlocks.CRIMSON_IRON_ORE.asBlockState()),
                                OreConfiguration.target(new BlockMatchTest(Blocks.BLACKSTONE), ModBlocks.BLACKSTONE_CRIMSON_IRON_ORE.asBlockState())
                        ),
                        8,
                        0
                )
        );
        PlacedFeature crimsonIronPlaced = new PlacedFeature(
                holder(crimsonIronFeature, ops, crimsonIronName),
                commonOrePlacement(14, PlacementUtils.RANGE_10_10)
        );

        // Azure Silver
        ConfiguredFeature<?, ?> azureSilverFeature = new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(
                        new TagMatchTest(Tags.Blocks.END_STONES),
                        ModBlocks.AZURE_SILVER_ORE.asBlockState(),
                        6,
                        0
                )
        );
        PlacedFeature azureSilverPlaced = new PlacedFeature(
                holder(azureSilverFeature, ops, azureSilverName),
                commonOrePlacement(8, HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(16), VerticalAnchor.absolute(92)
                        )
                )
        );

        // Collections of all configured features and placed features
        Map<ResourceLocation, ConfiguredFeature<?, ?>> oreFeatures = ImmutableMap.of(
                bortName, bortFeature,
                crimsonIronName, crimsonIronFeature,
                azureSilverName, azureSilverFeature
        );
        Map<ResourceLocation, PlacedFeature> orePlacedFeatures = ImmutableMap.of(
                bortName, bortPlaced,
                crimsonIronName, crimsonIronPlaced,
                azureSilverName, azureSilverPlaced
        );

        HolderSet<Biome> overworldBiomes = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_OVERWORLD);
        HolderSet<Biome> netherBiomes = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_NETHER);
        HolderSet<Biome> endBiomes = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_END);

        // Biome modifiers
        BiomeModifier overworldOres = new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworldBiomes,
                HolderSet.direct(
                        holderPlaced(bortPlaced, ops, bortName)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        );
        BiomeModifier netherOres = new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                netherBiomes,
                HolderSet.direct(
                        holderPlaced(crimsonIronPlaced, ops, crimsonIronName)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        );
        BiomeModifier endOres = new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                endBiomes,
                HolderSet.direct(
                        holderPlaced(azureSilverPlaced, ops, azureSilverName)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        );

        DataProvider configuredFeatureProvider = JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, SilentGear.MOD_ID, ops, Registry.CONFIGURED_FEATURE_REGISTRY,
                oreFeatures);
        DataProvider placedFeatureProvider = JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, SilentGear.MOD_ID, ops, Registry.PLACED_FEATURE_REGISTRY,
                orePlacedFeatures);
        DataProvider biomeModifierProvider = JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, SilentGear.MOD_ID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS,
                ImmutableMap.of(
                        SilentGear.getId("overworld_ores"), overworldOres,
                        SilentGear.getId("nether_ores"), netherOres,
                        SilentGear.getId("end_ores"), endOres
                )
        );

        generator.addProvider(true, configuredFeatureProvider);
        generator.addProvider(true, placedFeatureProvider);
        generator.addProvider(true, biomeModifierProvider);
    }

    public static Holder<ConfiguredFeature<?, ?>> holder(ConfiguredFeature<?, ?> feature, RegistryOps<JsonElement> ops, ResourceLocation location) {
        return ops.registryAccess.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).getOrCreateHolderOrThrow(ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, location));
    }

    public static Holder<PlacedFeature> holderPlaced(PlacedFeature feature, RegistryOps<JsonElement> ops, ResourceLocation location) {
        return ops.registryAccess.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY).getOrCreateHolderOrThrow(ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, location));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(count), modifier);
    }
}

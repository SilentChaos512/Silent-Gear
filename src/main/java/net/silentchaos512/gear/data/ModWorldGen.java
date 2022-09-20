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
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
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
        HolderSet.Named<Biome> overworld = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_OVERWORLD);

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
                holder(bortFeature, ops, SilentGear.getId("bort_ore")),
                commonOrePlacement(6, HeightRangePlacement.triangle(
                                VerticalAnchor.absolute(-60), VerticalAnchor.absolute(10)
                        )
                )
        );

        Map<ResourceLocation, ConfiguredFeature<?, ?>> oreFeatures = ImmutableMap.of(
                SilentGear.getId("bort_ore"), bortFeature
        );

        Map<ResourceLocation, PlacedFeature> orePlacedFeatures = ImmutableMap.of(
                SilentGear.getId("bort_ore"), bortPlaced
        );

        DataProvider configuredFeatureProvider = JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, SilentGear.MOD_ID, ops, Registry.CONFIGURED_FEATURE_REGISTRY, oreFeatures);

        DataProvider placedFeatureProvider = JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, SilentGear.MOD_ID, ops, Registry.PLACED_FEATURE_REGISTRY, orePlacedFeatures);

        BiomeModifier ores = new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworld,
                HolderSet.direct(holderPlaced(bortPlaced, ops, SilentGear.getId("bort_ore"))),
                GenerationStep.Decoration.UNDERGROUND_ORES
        );

        DataProvider biomeModifierProvider = JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, SilentGear.MOD_ID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS,
                ImmutableMap.of(SilentGear.getId("ores"), ores));

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

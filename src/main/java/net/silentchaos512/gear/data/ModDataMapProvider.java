package net.silentchaos512.gear.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    protected ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void gather() {
        final var compostables = builder(NeoForgeDataMaps.COMPOSTABLES);
        compostables.add(SgItems.FLAX_SEEDS, new Compostable(0.3f, true), false);
        compostables.add(SgItems.FLUFFY_SEEDS, new Compostable(0.3f, true), false);
        compostables.add(CraftingItems.FLAX_FIBER.asItem().builtInRegistryHolder(), new Compostable(0.5f, true), false);
        compostables.add(CraftingItems.FLUFFY_PUFF.asItem().builtInRegistryHolder(), new Compostable(0.5f, true), false);
        compostables.add(SgBlocks.NETHERWOOD_SAPLING.asItem().builtInRegistryHolder(), new Compostable(0.3f, true), false);
        compostables.add(SgItems.NETHER_BANANA, new Compostable(0.85f, true), false);
        compostables.add(SgItems.GOLDEN_NETHER_BANANA, new Compostable(1.0f, true), false);
        compostables.build();

        var furnaceFuels = builder(NeoForgeDataMaps.FURNACE_FUELS);
        furnaceFuels.add(CraftingItems.NETHERWOOD_STICK.asItem().builtInRegistryHolder(), new FurnaceFuel(150), false);
        furnaceFuels.add(SgItems.NETHERWOOD_CHARCOAL, new FurnaceFuel(2400), false);
        furnaceFuels.add(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK.asItem().builtInRegistryHolder(), new FurnaceFuel(24_000), false);
        furnaceFuels.build();
    }
}

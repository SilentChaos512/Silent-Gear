package net.silentchaos512.gear.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Transformable;
import net.silentchaos512.gear.setup.SgBlocks;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    protected ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        // Compostables and furnace fuels are item components as of 26.3 (see SgItemComponents)

        var transformables = builder(NeoForgeDataMaps.TRANSFORMABLES);
        transformables.add(SgBlocks.NETHERWOOD_LOG, Transformable.stripping(SgBlocks.NETHERWOOD_LOG.get(), SgBlocks.STRIPPED_NETHERWOOD_LOG.get()), false);
        transformables.add(SgBlocks.NETHERWOOD_WOOD, Transformable.stripping(SgBlocks.NETHERWOOD_WOOD.get(), SgBlocks.STRIPPED_NETHERWOOD_WOOD.get()), false);
        transformables.build();
    }
}

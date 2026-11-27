package net.silentchaos512.gear.data.loot;

import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.DatapackRegistryGatherer;

/**
 * Loot tables are a reloadable datapack registry as of 26.3, so they are gathered in
 * {@link net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent} instead of being a standalone provider.
 */
public final class ModLootTables {
    private ModLootTables() {}

    public static DatapackRegistryGatherer gather(DatapackRegistryGatherer gatherer) {
        return gatherer.lootTable(
                new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(ModChestLootTables::new, LootContextParamSets.CHEST),
                new SubProviderEntry(ModEntityLootTables::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(ModGiftLootTables::new, LootContextParamSets.GIFT)
        );
    }
}

package net.silentchaos512.gear.data.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

public class ModLootTables extends LootTableProvider {
    public ModLootTables(GatherDataEvent event) {
        super(
                event.getGenerator().getPackOutput(),
                Collections.emptySet(),
                VanillaLootTableProvider.create(event.getGenerator().getPackOutput(), event.getLookupProvider()).getTables(),
                event.getLookupProvider()
        );
    }

    @Override
    public List<SubProviderEntry> getTables() {
        return ImmutableList.of(
                new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(ModChestLootTables::new, LootContextParamSets.CHEST),
                new SubProviderEntry(ModEntityLootTables::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(ModGiftLootTables::new, LootContextParamSets.GIFT)
        );
    }

    @Override
    protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
    }
}

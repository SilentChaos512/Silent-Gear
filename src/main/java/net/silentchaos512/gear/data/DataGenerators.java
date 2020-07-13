package net.silentchaos512.gear.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.silentchaos512.gear.data.client.ModBlockStateProvider;
import net.silentchaos512.gear.data.client.ModItemModelProvider;
import net.silentchaos512.gear.data.loot.ModLootTables;
import net.silentchaos512.gear.data.material.MaterialsProvider;
import net.silentchaos512.gear.data.part.PartsProvider;
import net.silentchaos512.gear.data.recipes.ModRecipesProvider;

public final class DataGenerators {
    private DataGenerators() {}

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(new MaterialsProvider(gen));
        gen.addProvider(new PartsProvider(gen));

        gen.addProvider(new ModAdvancementProvider(gen));
        gen.addProvider(new ModBlockTagsProvider(gen));
        gen.addProvider(new ModItemTagsProvider(gen));
        gen.addProvider(new ModLootTables(gen));
        gen.addProvider(new ModRecipesProvider(gen));

        gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
    }
}

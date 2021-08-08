package net.silentchaos512.gear.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.data.client.CompoundModelsProvider;
import net.silentchaos512.gear.data.client.ModBlockStateProvider;
import net.silentchaos512.gear.data.client.ModItemModelProvider;
import net.silentchaos512.gear.data.loot.ModLootTables;
import net.silentchaos512.gear.data.material.MaterialsProvider;
import net.silentchaos512.gear.data.part.PartsProvider;
import net.silentchaos512.gear.data.recipes.ModRecipesProvider;
import net.silentchaos512.gear.data.trait.TraitsProvider;

public final class DataGenerators {
    private DataGenerators() {}

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        ModBlockTagsProvider blocks = new ModBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(blocks);
        gen.addProvider(new ModItemTagsProvider(gen, blocks, existingFileHelper));

        gen.addProvider(new TraitsProvider(gen));
        gen.addProvider(new MaterialsProvider(gen, SilentGear.MOD_ID));
        gen.addProvider(new PartsProvider(gen));

        gen.addProvider(new ModLootTables(gen));
        gen.addProvider(new ModRecipesProvider(gen));
        gen.addProvider(new ModAdvancementProvider(gen));

        gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
        gen.addProvider(new CompoundModelsProvider(gen, existingFileHelper));
    }
}

package net.silentchaos512.gear.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.data.client.CompoundModelsProvider;
import net.silentchaos512.gear.data.client.ModBlockStateProvider;
import net.silentchaos512.gear.data.client.ModItemModelProvider;
import net.silentchaos512.gear.data.loot.ModLootTables;
import net.silentchaos512.gear.data.material.MaterialsProvider;
import net.silentchaos512.gear.data.part.PartsProvider;
import net.silentchaos512.gear.data.recipes.ModRecipesProvider;
import net.silentchaos512.gear.data.trait.TraitsProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        ModBlockTagsProvider blocks = new ModBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(event.includeServer(), blocks);
        gen.addProvider(event.includeServer(), new ModItemTagsProvider(gen, blocks, existingFileHelper));

        gen.addProvider(event.includeServer(), new TraitsProvider(gen));
        gen.addProvider(event.includeServer(), new MaterialsProvider(gen, SilentGear.MOD_ID));
        gen.addProvider(event.includeServer(), new PartsProvider(gen));

        gen.addProvider(event.includeServer(), new ModLootTables(gen));
        gen.addProvider(event.includeServer(), new ModRecipesProvider(gen));
        gen.addProvider(event.includeServer(), new ModAdvancementProvider(gen));

        gen.addProvider(event.includeServer(), new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModItemModelProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new CompoundModelsProvider(gen, existingFileHelper));
    }
}

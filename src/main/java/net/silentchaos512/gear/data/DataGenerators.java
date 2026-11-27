package net.silentchaos512.gear.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import net.minecraft.data.CachedOutput;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.data.client.ModEquipmentAssetsProvider;
import net.silentchaos512.gear.data.client.ModModelProvider;
import net.silentchaos512.gear.data.loot.ModLootModifierProvider;
import net.silentchaos512.gear.data.loot.ModLootTables;
import net.silentchaos512.gear.data.recipes.ModRecipesProvider;
import net.silentchaos512.gear.data.tags.ModBlockTagsProvider;
import net.silentchaos512.gear.data.tags.ModDamageTypeTagsProvider;
import net.silentchaos512.gear.data.tags.ModEntityTypeTagsProvider;
import net.silentchaos512.gear.data.tags.ModItemTagsProvider;
import net.silentchaos512.gear.data.trait.TraitsProvider;
import net.silentchaos512.gear.setup.ItemComponentsSetup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        // Tags and other data providers use the world registries, like vanilla
        var lookupProvider = event.getWorldLookupProvider();

        generator.addProvider(true, new ModDataMapProvider(packOutput, lookupProvider));

        // Tags
        ModBlockTagsProvider blocks = new ModBlockTagsProvider(packOutput, lookupProvider);
        generator.addProvider(true, blocks);
        generator.addProvider(true, new ModItemTagsProvider(event, blocks));
        generator.addProvider(true, new ModDamageTypeTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new ModEntityTypeTagsProvider(packOutput, lookupProvider));

        // Gear data
        generator.addProvider(true, new TraitsProvider(lookupProvider, generator));
        generator.addProvider(true, new MaterialsProvider(lookupProvider, generator));
        generator.addProvider(true, new PartsProvider(generator));

        // Others
        generator.addProvider(true, new ModLootModifierProvider(packOutput, lookupProvider));
        // Recipes, loot tables, and advancements are datapack registries now, see gatherRegistryEntries
//        ModWorldGen.init(generator, existingFileHelper); //FIXME

        // Client
        generator.addProvider(true, new ModModelProvider(packOutput));
        generator.addProvider(true, new ModSoundDefinitionsProvider(packOutput));
        generator.addProvider(true, new ModEquipmentAssetsProvider(packOutput));
    }

    /**
     * Recipes, loot tables, and advancements became reloadable datapack registries in 26.3, so they are generated from
     * registry bootstraps instead of standalone data providers.
     */
    @SubscribeEvent
    public static void gatherRegistryEntries(GatherDataRegistryEntriesEvent event) {
        event.recipe(ModRecipesProvider::new)
                .advancement(ModAdvancementProvider::new)
                .add(Registries.CONTEXT_INT_PROVIDER, ItemComponentsSetup::bootstrapContextIntProviders);
        ModLootTables.gather(event);
    }

    public static CompletableFuture<?> saveStable(CachedOutput p_253653_, JsonElement p_254542_, Path p_254467_) {
        // Slightly modified version of DataProvider.saveStable. Only difference is that this one does not sort keys!
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");
                    GsonHelper.writeValue(jsonwriter, p_254542_, null);
                }

                p_253653_.writeIfNeeded(p_254467_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                SilentGear.LOGGER.error("Failed to save file to {}", p_254467_, ioexception);
            }

        }, Util.backgroundExecutor());
    }
}

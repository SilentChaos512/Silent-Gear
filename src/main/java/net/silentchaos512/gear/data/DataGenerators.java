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
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
import net.silentchaos512.lib.data.recipe.LibRecipeProvider;

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
        var lookupProvider = event.getLookupProvider();

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
        generator.addProvider(true, new ModLootTables(event));
        generator.addProvider(true, new ModLootModifierProvider(event));
        generator.addProvider(true, LibRecipeProvider.createRunner(packOutput, lookupProvider, "Silent Gear Recipes", ModRecipesProvider::new));
        generator.addProvider(true, new ModAdvancementProvider(packOutput, lookupProvider));
//        ModWorldGen.init(generator, existingFileHelper); //FIXME

        // Client
        generator.addProvider(true, new ModModelProvider(packOutput));
        generator.addProvider(true, new ModSoundDefinitionsProvider(packOutput));
        generator.addProvider(true, new ModEquipmentAssetsProvider(packOutput));
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

package net.silentchaos512.gear.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.GsonHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.data.client.CompoundModelsProvider;
import net.silentchaos512.gear.data.client.ModBlockStateProvider;
import net.silentchaos512.gear.data.client.ModItemModelProvider;
import net.silentchaos512.gear.data.loot.ModLootModifierProvider;
import net.silentchaos512.gear.data.loot.ModLootTables;
import net.silentchaos512.gear.data.recipes.ModRecipesProvider;
import net.silentchaos512.gear.data.trait.TraitsProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        ModBlockTagsProvider blocks = new ModBlockTagsProvider(event);
        gen.addProvider(true, blocks);
        gen.addProvider(true, new ModItemTagsProvider(event, blocks));

        gen.addProvider(true, new TraitsProvider(gen));
        gen.addProvider(true, new MaterialsProvider(gen, SilentGear.MOD_ID));
        gen.addProvider(true, new PartsProvider(gen));

        gen.addProvider(true, new ModLootTables(event));
        gen.addProvider(true, new ModLootModifierProvider(event));
        gen.addProvider(true, new ModRecipesProvider(event));
        gen.addProvider(true, new ModAdvancementProvider(event));
//        ModWorldGen.init(gen, existingFileHelper); //FIXME

        gen.addProvider(true, new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(true, new ModItemModelProvider(gen, existingFileHelper));
        gen.addProvider(true, new CompoundModelsProvider(gen, existingFileHelper));
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

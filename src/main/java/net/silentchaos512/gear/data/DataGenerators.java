package net.silentchaos512.gear.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.GsonHelper;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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
        ModWorldGen.init(gen, existingFileHelper);

        gen.addProvider(event.includeServer(), new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModItemModelProvider(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new CompoundModelsProvider(gen, existingFileHelper));
    }

    public static void saveStable(CachedOutput p_236073_, JsonElement p_236074_, Path p_236075_) throws IOException {
        // Slightly modified version of DataProvider.saveStable. Only difference is that this one does not sort keys!
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);
        Writer writer = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
        JsonWriter jsonwriter = new JsonWriter(writer);
        jsonwriter.setSerializeNulls(false);
        jsonwriter.setIndent("  ");
        GsonHelper.writeValue(jsonwriter, p_236074_, null);
        jsonwriter.close();
        p_236073_.writeIfNeeded(p_236075_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
    }
}

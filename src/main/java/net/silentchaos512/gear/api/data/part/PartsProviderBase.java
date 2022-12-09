package net.silentchaos512.gear.api.data.part;

import com.google.common.collect.Sets;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.data.DataGenerators;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public abstract class PartsProviderBase implements DataProvider {
    protected final DataGenerator generator;
    protected final String modId;

    public PartsProviderBase(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    private static void trySaveStable(CachedOutput cache, PartBuilder builder, Path path) {
        try {
            DataGenerators.saveStable(cache, builder.serialize(), path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public @NotNull String getName() {
        return "Silent Gear Parts: " + modId;
    }

    protected abstract Collection<PartBuilder> getParts();

    public void run(@NotNull CachedOutput cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Set<ResourceLocation> entries = Sets.newHashSet();

        //noinspection OverlyLongLambda
        getParts().forEach(builder -> {
            if (entries.contains(builder.getId())) {
                throw new IllegalStateException("Duplicate part: " + builder.getId());
            }

            // Part
            entries.add(builder.getId());
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_parts/%s.json", builder.getId().getNamespace(), builder.getId().getPath()));
            PartsProviderBase.trySaveStable(cache, builder, path);

            // Model
            // TODO
        });
    }
}

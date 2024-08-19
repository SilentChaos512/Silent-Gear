package net.silentchaos512.gear.api.data.part;

import com.google.common.collect.Sets;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class PartsProviderBase implements DataProvider {
    protected final DataGenerator generator;
    protected final String modId;

    public PartsProviderBase(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    public @NotNull String getName() {
        return "Silent Gear Parts: " + modId;
    }

    protected abstract Collection<PartBuilder> getParts();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path outputFolder = this.generator.getPackOutput().getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        this.getParts().forEach(builder -> {
            if (!set.add(builder.getId())) {
                throw new IllegalStateException("Duplicate part: " + builder.getId());
            }
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_parts/%s.json", builder.getId().getNamespace(), builder.getId().getPath()));
            list.add(DataProvider.saveStable(cache, builder.serialize(), path));
        });

        return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
    }
}

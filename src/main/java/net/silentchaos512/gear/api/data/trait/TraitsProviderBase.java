package net.silentchaos512.gear.api.data.trait;

import com.google.common.collect.Sets;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.data.DataGenerators;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class TraitsProviderBase implements DataProvider {
    protected final DataGenerator generator;
    protected final String modId;

    public TraitsProviderBase(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    @SuppressWarnings({"OverlyLongMethod", "MethodMayBeStatic"})
    public abstract Collection<TraitBuilder> getTraits();

    @Override
    public @NotNull String getName() {
        return "Silent Gear Traits: " + modId;
    }

    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path outputFolder = this.generator.getPackOutput().getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        this.getTraits().forEach(builder -> {
            ResourceLocation id = builder.getTrait().getId();
            if (!set.add(id)) {
                throw new IllegalStateException("Duplicate trait: " + id);
            }
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_traits/%s.json", id.getNamespace(), id.getPath()));
            list.add(DataGenerators.saveStable(cache, builder.serialize(), path));
        });

        return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
    }
}

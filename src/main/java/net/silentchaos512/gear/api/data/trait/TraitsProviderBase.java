package net.silentchaos512.gear.api.data.trait;

import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.trait.Trait;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class TraitsProviderBase implements DataProvider {
    protected final CompletableFuture<HolderLookup.Provider> lookupProvider;
    protected final DataGenerator generator;
    protected final String modId;

    public TraitsProviderBase(CompletableFuture<HolderLookup.Provider> lookupProvider, DataGenerator generator, String modId) {
        this.lookupProvider = lookupProvider;
        this.generator = generator;
        this.modId = modId;
    }

    @SuppressWarnings({"OverlyLongMethod", "MethodMayBeStatic"})
    public abstract Collection<TraitBuilder> getTraits(HolderLookup.Provider registries);

    @Override
    public @NotNull String getName() {
        return "Silent Gear Traits: " + modId;
    }

    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path outputFolder = this.generator.getPackOutput().getOutputFolder();
        Set<Identifier> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        return this.lookupProvider.thenCompose(provider -> {
            this.getTraits(provider).forEach(builder -> {
                Identifier id = builder.getTraitHolder().getId();
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate trait: " + id);
                }
                Path path = outputFolder.resolve(String.format("data/%s/silentgear_traits/%s.json", id.getNamespace(), id.getPath()));
                SilentGear.LOGGER.info("Serializing trait \"{}\"", id);
                list.add(DataProvider.saveStable(cache, provider, Trait.CODEC, builder.build(), path));
            });

            return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
        });
    }
}

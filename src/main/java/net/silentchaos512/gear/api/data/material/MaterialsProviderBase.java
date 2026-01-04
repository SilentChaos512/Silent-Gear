package net.silentchaos512.gear.api.data.material;

import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class MaterialsProviderBase implements DataProvider {
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    protected final DataGenerator generator;
    protected final String modId;

    public MaterialsProviderBase(CompletableFuture<HolderLookup.Provider> lookupProvider, DataGenerator generator, String modId) {
        this.lookupProvider = lookupProvider;
        this.generator = generator;
        this.modId = modId;
    }

    protected abstract Collection<MaterialBuilder<?>> getMaterials(HolderLookup.Provider registries);

    protected DataResource<Material> modId(String path) {
        return DataResource.material(Identifier.fromNamespaceAndPath(this.modId, path));
    }

    @SuppressWarnings("WeakerAccess")
    protected static Identifier commonId(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }

    @Override
    public @NotNull String getName() {
        return "Silent Gear Materials: " + modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path outputFolder = this.generator.getPackOutput().getOutputFolder();
        Set<Identifier> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        return this.lookupProvider.thenCompose(provider -> {
            this.getMaterials(provider).forEach(builder -> {
                var id = builder.getId();
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate material: " + id);
                }
                Path path = outputFolder.resolve(String.format("data/%s/silentgear_materials/%s.json", id.getNamespace(), id.getPath()));
                SilentGear.LOGGER.info("Serializing material \"{}\"", id);
                list.add(DataProvider.saveStable(cache, provider, MaterialSerializers.DISPATCH_CODEC, builder.build(), path));
            });

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }
}

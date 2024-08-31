package net.silentchaos512.gear.api.data.material;

import com.google.common.collect.Sets;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.util.DataResource;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class MaterialsProviderBase implements DataProvider {
    protected final DataGenerator generator;
    protected final String modId;

    public MaterialsProviderBase(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    protected abstract Collection<MaterialBuilder<?>> getMaterials();

    protected DataResource<Material> modId(String path) {
        return DataResource.material(ResourceLocation.fromNamespaceAndPath(this.modId, path));
    }

    @SuppressWarnings("WeakerAccess")
    protected static ResourceLocation commonId(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    @Override
    public @NotNull String getName() {
        return "Silent Gear Materials: " + modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path outputFolder = this.generator.getPackOutput().getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        this.getMaterials().forEach(builder -> {
            if (!set.add(builder.getId())) {
                throw new IllegalStateException("Duplicate material: " + builder.getId());
            }
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_materials/%s.json", builder.getId().getNamespace(), builder.getId().getPath()));
            list.add(DataProvider.saveStable(cache, builder.serialize(), path));
        });

        return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
    }
}

package net.silentchaos512.gear.api.data.material;

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

public abstract class MaterialsProviderBase implements DataProvider {
    protected final DataGenerator generator;
    protected final String modId;

    public MaterialsProviderBase(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    protected abstract Collection<MaterialBuilder> getMaterials();

    protected ResourceLocation modId(String path) {
        return new ResourceLocation(this.modId, path);
    }

    @SuppressWarnings("WeakerAccess")
    protected static ResourceLocation forgeId(String path) {
        return new ResourceLocation("forge", path);
    }

    @Override
    public @NotNull String getName() {
        return "Silent Gear Materials: " + modId;
    }

    @Override
    public void run(@NotNull CachedOutput cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Set<ResourceLocation> entries = Sets.newHashSet();

        //noinspection OverlyLongLambda
        getMaterials().forEach(builder -> {
            if (entries.contains(builder.getId())) {
                throw new IllegalStateException("Duplicate material: " + builder.getId());
            }

            // Material
            entries.add(builder.getId());
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_materials/%s.json", builder.getId().getNamespace(), builder.getId().getPath()));
            MaterialsProviderBase.trySaveStable(cache, builder, path);

            // Model
            // TODO
        });
    }

    private static void trySaveStable(CachedOutput cache, MaterialBuilder builder, Path path) {
        try {
            DataGenerators.saveStable(cache, builder.serialize(), path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

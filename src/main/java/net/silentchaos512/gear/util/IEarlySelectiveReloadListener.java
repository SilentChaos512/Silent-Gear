package net.silentchaos512.gear.util;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.resource.IResourceType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public interface IEarlySelectiveReloadListener extends PreparableReloadListener {
    @Override
    default CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            this.onResourceManagerReload(resourceManager, t -> true /*SelectiveReloadStateHandler.INSTANCE.get()*/); // FIXME?
        }, backgroundExecutor).thenCompose(stage::wait);
    }

    void onResourceManagerReload(ResourceManager resourceManager, Predicate<IResourceType> predicate);
}

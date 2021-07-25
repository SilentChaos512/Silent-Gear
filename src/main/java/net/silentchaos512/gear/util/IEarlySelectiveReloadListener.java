package net.silentchaos512.gear.util;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.SelectiveReloadStateHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public interface IEarlySelectiveReloadListener extends PreparableReloadListener {
    @Override
    default CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            this.onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
        }, backgroundExecutor).thenCompose(stage::wait);
    }

    void onResourceManagerReload(ResourceManager resourceManager, Predicate<IResourceType> predicate);
}

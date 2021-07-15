package net.silentchaos512.gear.util;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.SelectiveReloadStateHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public interface IEarlySelectiveReloadListener extends IFutureReloadListener {
    @Override
    default CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            this.onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
        }, backgroundExecutor).thenCompose(stage::wait);
    }

    void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> predicate);
}

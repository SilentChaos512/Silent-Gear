package net.silentchaos512.gear.util;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface IEarlySelectiveReloadListener extends PreparableReloadListener {
    @Override
    default CompletableFuture<Void> reload(SharedState sharedState, Executor executor, PreparationBarrier barrier, Executor applyExecutor) {
        return CompletableFuture.runAsync(() -> {
            this.onResourceManagerReload(sharedState.resourceManager());
        }, executor).thenCompose(barrier::wait);
    }

    void onResourceManagerReload(ResourceManager resourceManager);
}

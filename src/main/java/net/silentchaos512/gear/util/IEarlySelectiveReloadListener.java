package net.silentchaos512.gear.util;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface IEarlySelectiveReloadListener extends PreparableReloadListener {
    @Nonnull
    @Override
    default CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            this.onResourceManagerReload(resourceManager);
        }, backgroundExecutor).thenCompose(barrier::wait);
    }

    void onResourceManagerReload(ResourceManager resourceManager);
}

package net.silentchaos512.gear.network.server;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.silentchaos512.gear.network.client.AckPayload;

import java.util.concurrent.CompletableFuture;

public class SgServerPayloadHandler {
    private static final SgServerPayloadHandler INSTANCE = new SgServerPayloadHandler();

    public static SgServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static CompletableFuture<Void> handleData(final PlayPayloadContext ctx, Runnable handler) {
        return ctx.workHandler().submitAsync(handler)
                .exceptionally(e -> {
                    ctx.packetHandler().disconnect(Component.translatable("network.silentgear.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleAck(AckPayload data, ConfigurationPayloadContext ctx) {
        ctx.taskCompletedHandler().onTaskCompleted(data.type());
    }
}

package net.silentchaos512.gear.network.client;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.network.config.SyncTraitsConfigurationTask;
import net.silentchaos512.gear.network.server.SPacketSyncMaterials;
import net.silentchaos512.gear.network.server.SPacketSyncParts;
import net.silentchaos512.gear.network.server.SPacketSyncTraits;

import java.util.concurrent.CompletableFuture;

public class SgClientPayloadHandler {
    private static final SgClientPayloadHandler INSTANCE = new SgClientPayloadHandler();

    public static SgClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static CompletableFuture<Void> handleConfigurationData(final ConfigurationPayloadContext ctx, Runnable handler) {
        return ctx.workHandler().submitAsync(handler)
                .exceptionally(e -> {
                    ctx.packetHandler().disconnect(Component.translatable("network.silentgear.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleSyncTraits(SPacketSyncTraits data, ConfigurationPayloadContext ctx) {
        handleConfigurationData(ctx, () -> TraitManager.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.replyHandler().send(new AckPayload(SyncTraitsConfigurationTask.TYPE)));
    }

    public void handleSyncMaterials(SPacketSyncMaterials data, ConfigurationPayloadContext ctx) {
        handleConfigurationData(ctx, () -> MaterialManager.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.replyHandler().send(new AckPayload(SyncMaterialsConfigurationTask.TYPE)));
    }

    public void handleSyncParts(SPacketSyncParts data, ConfigurationPayloadContext ctx) {
        handleConfigurationData(ctx, () -> PartManager.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.replyHandler().send(new AckPayload(SyncPartsConfigurationTask.TYPE)));
    }
}

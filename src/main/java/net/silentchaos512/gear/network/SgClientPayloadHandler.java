package net.silentchaos512.gear.network;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.command.MaterialsCommand;
import net.silentchaos512.gear.command.TraitsCommand;
import net.silentchaos512.gear.network.payload.client.AckPayload;
import net.silentchaos512.gear.network.payload.server.CommandOutputPayload;
import net.silentchaos512.gear.network.payload.server.SyncMaterialsPayload;
import net.silentchaos512.gear.network.payload.server.SyncPartsPayload;
import net.silentchaos512.gear.network.payload.server.SyncTraitsPayload;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.concurrent.CompletableFuture;

public class SgClientPayloadHandler {
    private static final SgClientPayloadHandler INSTANCE = new SgClientPayloadHandler();

    public static SgClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static CompletableFuture<Void> handleData(final IPayloadContext ctx, Runnable handler) {
        return ctx.enqueueWork(handler)
                .exceptionally(e -> {
                    ctx.disconnect(Component.translatable("network.silentgear.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleSyncTraits(final SyncTraitsPayload data, final IPayloadContext ctx) {
        handleData(ctx, () -> SgRegistries.TRAIT.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.reply(new AckPayload()));
    }

    public void handleSyncMaterials(final SyncMaterialsPayload data, final IPayloadContext ctx) {
        handleData(ctx, () -> SgRegistries.MATERIAL.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.reply(new AckPayload()));
    }

    public void handleSyncParts(final SyncPartsPayload data, final IPayloadContext ctx) {
        handleData(ctx, () -> SgRegistries.PART.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.reply(new AckPayload()));
    }

    public void handleCommandOutput(CommandOutputPayload data, IPayloadContext ctx) {
        switch (data.commandType()) {
            case MATERIALS:
                MaterialsCommand.runDumpClient(data.includeChildren());
                break;
            case TRAITS:
                TraitsCommand.runDumpMdClient();
                break;
            default:
                SilentGear.LOGGER.error("Unknown ClientOutputCommandPacket.Type: {}", data.commandType());
        }
    }
}

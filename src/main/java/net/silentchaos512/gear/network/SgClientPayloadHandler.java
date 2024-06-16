package net.silentchaos512.gear.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.command.MaterialsCommand;
import net.silentchaos512.gear.command.TraitsCommand;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.network.configtask.SyncMaterialsConfigurationTask;
import net.silentchaos512.gear.network.configtask.SyncPartsConfigurationTask;
import net.silentchaos512.gear.network.configtask.SyncTraitsConfigurationTask;
import net.silentchaos512.gear.network.payload.client.AckPayload;
import net.silentchaos512.gear.network.payload.server.*;
import net.silentchaos512.gear.util.TextUtil;

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

    private static void handleData(final PlayPayloadContext ctx, Runnable handler) {
        ctx.workHandler().submitAsync(handler)
                .exceptionally(e -> {
                    ctx.packetHandler().disconnect(Component.translatable("network.silentgear.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleSyncTraits(SyncTraitsPayload data, ConfigurationPayloadContext ctx) {
        handleConfigurationData(ctx, () -> TraitManager.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.replyHandler().send(new AckPayload(SyncTraitsConfigurationTask.TYPE)));
    }

    public void handleSyncMaterials(SyncMaterialsPayload data, ConfigurationPayloadContext ctx) {
        handleConfigurationData(ctx, () -> MaterialManager.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.replyHandler().send(new AckPayload(SyncMaterialsConfigurationTask.TYPE)));
    }

    public void handleSyncParts(SyncPartsPayload data, ConfigurationPayloadContext ctx) {
        handleConfigurationData(ctx, () -> PartManager.handleSyncPacket(data, ctx))
                .thenAccept(v -> ctx.replyHandler().send(new AckPayload(SyncPartsConfigurationTask.TYPE)));
    }

    public void handleCommandOutput(CommandOutputPayload data, PlayPayloadContext ctx) {
        switch (data.type()) {
            case MATERIALS:
                MaterialsCommand.runDumpClient(data.includeChildren());
                break;
            case TRAITS:
                TraitsCommand.runDumpMdClient();
                break;
            default:
                SilentGear.LOGGER.error("Unknown ClientOutputCommandPacket.Type: {}", data.type());
        }
    }

    public void handleOpenGuideBook(OpenGuideBookPayload data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                // Open a guide book if I ever program one lol
                // Instead, just display a message that links the GitHub wiki
                String wikiUrl = "https://github.com/SilentChaos512/Silent-Gear/wiki";
                player.sendSystemMessage(TextUtil.translate("item", "guide_book.unimplemented1"));
                player.sendSystemMessage(TextUtil.translate("item", "guide_book.check_wiki")
                        .append(Component.literal(wikiUrl)
                                .withStyle(ChatFormatting.UNDERLINE)
                                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                        wikiUrl)))));
            });
        });
    }
}

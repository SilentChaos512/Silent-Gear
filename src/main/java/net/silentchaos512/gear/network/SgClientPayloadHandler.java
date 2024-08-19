package net.silentchaos512.gear.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.command.MaterialsCommand;
import net.silentchaos512.gear.command.TraitsCommand;
import net.silentchaos512.gear.network.payload.client.AckPayload;
import net.silentchaos512.gear.network.payload.server.*;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.TextUtil;

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

    public void handleOpenGuideBook(OpenGuideBookPayload data, IPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player();
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
    }
}

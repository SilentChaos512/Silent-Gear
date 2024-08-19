package net.silentchaos512.gear.network;

import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.network.payload.client.*;
import net.silentchaos512.gear.network.payload.server.*;

@EventBusSubscriber(modid = SilentGear.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class SgNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("4.0");
        // Data resource sync packets
        registrar.playToClient(
                SyncTraitsPayload.TYPE,
                SyncTraitsPayload.STREAM_CODEC,
                (data, ctx) -> SgClientPayloadHandler.getInstance().handleSyncTraits(data, ctx)
        );
        registrar.playToClient(
                SyncMaterialsPayload.TYPE,
                SyncMaterialsPayload.STREAM_CODEC,
                (data, ctx) -> SgClientPayloadHandler.getInstance().handleSyncMaterials(data, ctx)
        );
        registrar.playToClient(
                SyncPartsPayload.TYPE,
                SyncPartsPayload.STREAM_CODEC,
                (data, ctx) -> SgClientPayloadHandler.getInstance().handleSyncParts(data, ctx)
        );
        registrar.playToServer(
                AckPayload.TYPE,
                StreamCodec.unit(new AckPayload()),
                (data, ctx) -> SgServerPayloadHandler.getInstance().handleAck(data, ctx)
        );

        // Play phase server 2 client packets
        registrar.playToClient(
                CommandOutputPayload.TYPE,
                CommandOutputPayload.STREAM_CODEC,
                (data, ctx) -> SgClientPayloadHandler.getInstance().handleCommandOutput(data, ctx)
        );
        registrar.playToClient(
                OpenGuideBookPayload.TYPE,
                StreamCodec.unit(new OpenGuideBookPayload()),
                (data, ctx) -> SgClientPayloadHandler.getInstance().handleOpenGuideBook(data, ctx)
        );

        // Play phase client 2 server play packets
        registrar.playToServer(
                AlloyMakerUpdatePayload.TYPE,
                AlloyMakerUpdatePayload.STREAM_CODEC,
                (data, ctx) -> SgServerPayloadHandler.getInstance().handleAlloyMakerUpdate(data, ctx)
        );
        registrar.playToServer(
                SwingGearPayload.TYPE,
                StreamCodec.unit(new SwingGearPayload()),
                (data, ctx) -> SgServerPayloadHandler.getInstance().handleSwingGear(data, ctx)
        );
        registrar.playToServer(
                KeyPressOnItemPayload.TYPE,
                KeyPressOnItemPayload.STREAM_CODEC,
                (data, ctx) -> SgServerPayloadHandler.getInstance().handleKeyPressOnItem(data, ctx)
        );
        registrar.playToServer(
                RecalculateStatsPayload.TYPE,
                RecalculateStatsPayload.STREAM_CODEC,
                (data, ctx) -> SgServerPayloadHandler.getInstance().handleRecalculateStats(data, ctx)
        );
        registrar.playToServer(
                SelectBlueprintInBookPayload.TYPE,
                SelectBlueprintInBookPayload.STREAM_CODEC,
                (data, ctx) -> SgServerPayloadHandler.getInstance().handleSelectBlueprintInBook(data, ctx)
        );
    }
}

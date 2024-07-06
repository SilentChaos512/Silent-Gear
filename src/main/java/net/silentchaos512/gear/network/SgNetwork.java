package net.silentchaos512.gear.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.silentchaos512.gear.network.payload.client.*;
import net.silentchaos512.gear.network.payload.server.*;

public final class SgNetwork {
    public static void register(PayloadRegistrar registrar) {
        // Configuration phase packets
        registrar.configurationBidirectional(SyncTraitsPayload.ID,
                SyncTraitsPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncTraits));
        registrar.configurationBidirectional(SyncMaterialsPayload.ID,
                SyncMaterialsPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncMaterials));
        registrar.configurationBidirectional(SyncPartsPayload.ID,
                SyncPartsPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncParts));

        registrar.configurationBidirectional(AckPayload.ID,
                AckPayload::new,
                handler -> handler.server((SgServerPayloadHandler.getInstance()::handleAck)));

        // Play phase server 2 client packets
        registrar.playToClient(CommandOutputPayload.ID,
                CommandOutputPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleCommandOutput));
        registrar.playToClient(OpenGuideBookPayload.ID,
                OpenGuideBookPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleOpenGuideBook));

        // Play phase client 2 server play packets
        registrar.playToServer(AlloyMakerUpdatePayload.ID,
                AlloyMakerUpdatePayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleAlloyMakerUpdate));
        registrar.playToServer(SwingGearPayload.ID,
                SwingGearPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleSwingGear));
        registrar.playToServer(KeyPressOnItemPayload.ID,
                KeyPressOnItemPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleKeyPressOnItem));
        registrar.playToServer(RecalculateStatsPayload.ID,
                RecalculateStatsPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleRecalculateStats));
        registrar.playToServer(SelectBlueprintInBookPayload.ID,
                SelectBlueprintInBookPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleSelectBlueprintInBook));
    }
}

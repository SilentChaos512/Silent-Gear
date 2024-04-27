package net.silentchaos512.gear.network;

import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.silentchaos512.gear.network.client.*;
import net.silentchaos512.gear.network.payload.client.*;
import net.silentchaos512.gear.network.payload.server.*;
import net.silentchaos512.gear.network.payload.server.server.*;
import net.silentchaos512.gear.network.server.*;

public final class SgNetwork {
    public static void register(IPayloadRegistrar registrar) {
        // Configuration phase packets
        registrar.configuration(SyncTraitsPayload.ID,
                SyncTraitsPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncTraits));
        registrar.configuration(SyncMaterialsPayload.ID,
                SyncMaterialsPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncMaterials));
        registrar.configuration(SyncPartsPayload.ID,
                SyncPartsPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncParts));

        registrar.configuration(AckPayload.ID,
                AckPayload::new,
                handler -> handler.server((SgServerPayloadHandler.getInstance()::handleAck)));

        // Play phase server 2 client packets
        registrar.play(CommandOutputPayload.ID,
                CommandOutputPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleCommandOutput));
        registrar.play(OpenGuideBookPayload.ID,
                OpenGuideBookPayload::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleOpenGuideBook));

        // Play phase client 2 server play packets
        registrar.play(AlloyMakerUpdatePayload.ID,
                AlloyMakerUpdatePayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleAlloyMakerUpdate));
        registrar.play(SwingGearPayload.ID,
                SwingGearPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleSwingGear));
        registrar.play(KeyPressOnItemPayload.ID,
                KeyPressOnItemPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleKeyPressOnItem));
        registrar.play(RecalculateStatsPayload.ID,
                RecalculateStatsPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleRecalculateStats));
        registrar.play(SelectBlueprintInBookPayload.ID,
                SelectBlueprintInBookPayload::new,
                handler -> handler.server(SgServerPayloadHandler.getInstance()::handleSelectBlueprintInBook));
    }
}

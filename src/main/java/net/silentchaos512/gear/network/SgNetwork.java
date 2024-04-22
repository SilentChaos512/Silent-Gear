package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.network.client.AckPayload;
import net.silentchaos512.gear.network.client.SgClientPayloadHandler;
import net.silentchaos512.gear.network.server.SPacketSyncMaterials;
import net.silentchaos512.gear.network.server.SPacketSyncParts;
import net.silentchaos512.gear.network.server.SPacketSyncTraits;
import net.silentchaos512.gear.network.server.SgServerPayloadHandler;
import net.silentchaos512.gear.util.MismatchedVersionsException;

import java.util.Objects;

public final class SgNetwork {
    public static void register(IPayloadRegistrar registrar) {
        registrar.configuration(SPacketSyncTraits.ID,
                SPacketSyncTraits::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncTraits));
        registrar.configuration(SPacketSyncMaterials.ID,
                SPacketSyncMaterials::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncMaterials));
        registrar.configuration(SPacketSyncParts.ID,
                SPacketSyncParts::new,
                handler -> handler.client(SgClientPayloadHandler.getInstance()::handleSyncParts));

        registrar.configuration(AckPayload.ID,
                AckPayload::new,
                handler -> handler.server((SgServerPayloadHandler.getInstance()::handleAck)));
    }

    static {
        channel.messageBuilder(SyncGearCraftingItemsPacket.class, 4)
                .encoder(SyncGearCraftingItemsPacket::toBytes)
                .decoder(SyncGearCraftingItemsPacket::fromBytes)
                .consumerMainThread(SyncGearCraftingItemsPacket::handle)
                .add();

        channel.messageBuilder(SyncMaterialCraftingItemsPacket.class, 8)
                .decoder(SyncMaterialCraftingItemsPacket::decode)
                .encoder(SyncMaterialCraftingItemsPacket::encode)
                .consumerMainThread(SyncMaterialCraftingItemsPacket::handle)
                .add();
        channel.messageBuilder(KeyPressOnItemPacket.class, 9, NetworkDirection.PLAY_TO_SERVER)
                .decoder(KeyPressOnItemPacket::decode)
                .encoder(KeyPressOnItemPacket::encode)
                .consumerMainThread(KeyPressOnItemPacket::handle)
                .add();
        channel.messageBuilder(SelectBlueprintFromBookPacket.class, 10, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SelectBlueprintFromBookPacket::decode)
                .encoder(SelectBlueprintFromBookPacket::encode)
                .consumerMainThread(SelectBlueprintFromBookPacket::handle)
                .add();
        channel.messageBuilder(ProspectingResultPacket.class, 11, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ProspectingResultPacket::decode)
                .encoder(ProspectingResultPacket::encode)
                .consumerMainThread(ProspectingResultPacket::handle)
                .add();
        channel.messageBuilder(GearLeftClickPacket.class, 12, NetworkDirection.PLAY_TO_SERVER)
                .decoder(GearLeftClickPacket::decode)
                .encoder(GearLeftClickPacket::encode)
                .consumerMainThread(GearLeftClickPacket::handle)
                .add();
        channel.messageBuilder(ClientOutputCommandPacket.class, 13)
                .encoder(ClientOutputCommandPacket::encode)
                .decoder(ClientOutputCommandPacket::decode)
                .consumerMainThread(ClientOutputCommandPacket::handle)
                .add();
        channel.messageBuilder(CompounderUpdatePacket.class, 14, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CompounderUpdatePacket::encode)
                .decoder(CompounderUpdatePacket::decode)
                .consumerMainThread(CompounderUpdatePacket::handle)
                .add();
        channel.messageBuilder(OpenGuideBookPacket.class, 15, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((pkt, buf) -> {})
                .decoder(buf -> new OpenGuideBookPacket())
                .consumerMainThread(OpenGuideBookPacket::handle)
                .add();
        channel.messageBuilder(RecalculateStatsPacket.class, 16, NetworkDirection.PLAY_TO_SERVER)
                .decoder(RecalculateStatsPacket::decode)
                .encoder(RecalculateStatsPacket::encode)
                .consumerMainThread(RecalculateStatsPacket::handle)
                .add();
    }

    private SgNetwork() {}

    public static void init() {}

    public static void writeModVersionInfoToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(SgNetwork.VERSION); // Change to test error message (dedicated server only)
        buffer.writeUtf(SilentGear.getVersion());
    }

    public static void verifyNetworkVersion(FriendlyByteBuf buffer) {
        // Throws an exception if versions do not match and provides a less cryptic message to the player
        // NOTE: This hangs without displaying a message on SSP, but that can't happen without messing with the written
        // network version
        String serverNetVersion = readNetworkVersion(buffer);
        String serverModVersion = readModVersion(buffer);

        SilentGear.LOGGER.debug("Read Silent Gear server version as {} ({})", serverModVersion, serverNetVersion);

        if (!SgNetwork.VERSION.equals(serverNetVersion)) {
            String msg = String.format("This server is running a different version of Silent Gear. Try updating Silent Gear on the client and/or server. Client version is %s (%s) and server version is %s (%s).",
                    SilentGear.getVersion(),
                    SgNetwork.VERSION,
                    serverModVersion,
                    serverNetVersion);
            throw new MismatchedVersionsException(msg);
        }
    }

    private static String readNetworkVersion(FriendlyByteBuf buffer) {
        String str = buffer.readUtf(16);
        if (!NET_VERSION_PATTERN.matcher(str).matches()) {
            // Server is running a version that doesn't encode the net version
            return "UNKNOWN (received: " + str + ")";
        }
        return str;
    }

    private static String readModVersion(FriendlyByteBuf buffer) {
        String str = buffer.readUtf(16);
        if (!"NONE".equals(str) && !MOD_VERSION_PATTERN.matcher(str).matches()) {
            // Server is running a version that doesn't encode the mod version
            return "UNKNOWN (received: " + str + ")";
        }
        return str;
    }
}

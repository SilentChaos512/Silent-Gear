package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.FMLHandshakeHandler;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.util.MismatchedVersionsException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Network {
    public static final String VERSION = "sgear-net-11";
    private static final Pattern NET_VERSION_PATTERN = Pattern.compile("sgear-net-\\d+$");
    private static final Pattern MOD_VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");

    public static SimpleChannel channel;

    static {
        channel = NetworkRegistry.ChannelBuilder.named(SilentGear.getId("network"))
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        channel.messageBuilder(SyncTraitsPacket.class, 1)
                .loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                .decoder(SyncTraitsPacket::fromBytes)
                .encoder(SyncTraitsPacket::toBytes)
                .markAsLoginPacket()
                .consumer(FMLHandshakeHandler.biConsumerFor((hh, msg, ctx) -> {
                    TraitManager.handleTraitSyncPacket(msg, ctx);
                    channel.reply(new LoginPacket.Reply(), ctx.get());
                }))
                .add();
        channel.messageBuilder(SyncGearPartsPacket.class, 2)
                .loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                .decoder(SyncGearPartsPacket::fromBytes)
                .encoder(SyncGearPartsPacket::toBytes)
                .markAsLoginPacket()
                .consumer(FMLHandshakeHandler.biConsumerFor((hh, msg, ctx) -> {
                    PartManager.handlePartSyncPacket(msg, ctx);
                    channel.reply(new LoginPacket.Reply(), ctx.get());
                }))
                .add();
        channel.messageBuilder(LoginPacket.Reply.class, 3)
                .loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                .decoder(buffer -> new LoginPacket.Reply())
                .encoder((msg, buffer) -> {})
                .consumer(FMLHandshakeHandler.indexFirst((hh, msg, ctx) -> msg.handle(ctx)))
                .add();
        channel.messageBuilder(SyncGearCraftingItemsPacket.class, 4)
                .encoder(SyncGearCraftingItemsPacket::toBytes)
                .decoder(SyncGearCraftingItemsPacket::fromBytes)
                .consumer(SyncGearCraftingItemsPacket::handle)
                .add();
        // 5 was ShowPartsScreenPacket
        channel.messageBuilder(SyncMaterialsPacket.class, 6)
                .loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                .decoder(SyncMaterialsPacket::fromBytes)
                .encoder(SyncMaterialsPacket::toBytes)
                .markAsLoginPacket()
                .consumer(FMLHandshakeHandler.biConsumerFor((hh, msg, ctx) -> {
                    MaterialManager.handleSyncPacket(msg, ctx);
                    channel.reply(new LoginPacket.Reply(), ctx.get());
                }))
                .add();
        // uwu
        channel.messageBuilder(FMLPlayMessages.SpawnEntity.class, 7)
                .encoder(FMLPlayMessages.SpawnEntity::encode)
                .decoder(FMLPlayMessages.SpawnEntity::decode)
                .consumer(FMLPlayMessages.SpawnEntity::handle)
                .add();
        channel.messageBuilder(SyncMaterialCraftingItemsPacket.class, 8)
                .decoder(SyncMaterialCraftingItemsPacket::decode)
                .encoder(SyncMaterialCraftingItemsPacket::encode)
                .consumer(SyncMaterialCraftingItemsPacket::handle)
                .add();
        channel.messageBuilder(KeyPressOnItemPacket.class, 9, NetworkDirection.PLAY_TO_SERVER)
                .decoder(KeyPressOnItemPacket::decode)
                .encoder(KeyPressOnItemPacket::encode)
                .consumer(KeyPressOnItemPacket::handle)
                .add();
        channel.messageBuilder(SelectBlueprintFromBookPacket.class, 10, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SelectBlueprintFromBookPacket::decode)
                .encoder(SelectBlueprintFromBookPacket::encode)
                .consumer(SelectBlueprintFromBookPacket::handle)
                .add();
        channel.messageBuilder(ProspectingResultPacket.class, 11, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ProspectingResultPacket::decode)
                .encoder(ProspectingResultPacket::encode)
                .consumer(ProspectingResultPacket::handle)
                .add();
        channel.messageBuilder(GearLeftClickPacket.class, 12, NetworkDirection.PLAY_TO_SERVER)
                .decoder(GearLeftClickPacket::decode)
                .encoder(GearLeftClickPacket::encode)
                .consumer(GearLeftClickPacket::handle)
                .add();
        channel.messageBuilder(ClientOutputCommandPacket.class, 13)
                .encoder(ClientOutputCommandPacket::encode)
                .decoder(ClientOutputCommandPacket::decode)
                .consumer(ClientOutputCommandPacket::handle)
                .add();
        channel.messageBuilder(CompounderUpdatePacket.class, 14, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CompounderUpdatePacket::encode)
                .decoder(CompounderUpdatePacket::decode)
                .consumer(CompounderUpdatePacket::handle)
                .add();
        channel.messageBuilder(OpenGuideBookPacket.class, 15, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((pkt, buf) -> {})
                .decoder(buf -> new OpenGuideBookPacket())
                .consumer(OpenGuideBookPacket::handle)
                .add();
    }

    private Network() {}

    public static void init() {}

    static void writeModVersionInfoToNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(Network.VERSION); // Change to test error message (dedicated server only)
        buffer.writeUtf(SilentGear.getVersion());
    }

    static void verifyNetworkVersion(FriendlyByteBuf buffer) {
        // Throws an exception if versions do not match and provides a less cryptic message to the player
        // NOTE: This hangs without displaying a message on SSP, but that can't happen without messing with the written
        // network version
        String serverNetVersion = readNetworkVersion(buffer);
        String serverModVersion = readModVersion(buffer);

        SilentGear.LOGGER.debug("Read Silent Gear server version as {} ({})", serverModVersion, serverNetVersion);

        if (!Network.VERSION.equals(serverNetVersion)) {
            String msg = String.format("This server is running a different version of Silent Gear. Try updating Silent Gear on the client and/or server. Client version is %s (%s) and server version is %s (%s).",
                    SilentGear.getVersion(),
                    Network.VERSION,
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

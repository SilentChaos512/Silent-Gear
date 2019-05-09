package net.silentchaos512.gear.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitManager;

import java.util.Objects;

public class Network {
    private static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "network");
    private static final int VERSION = 2;

    public static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
                .clientAcceptedVersions(s -> Objects.equals(s, String.valueOf(VERSION)))
                .serverAcceptedVersions(s -> Objects.equals(s, String.valueOf(VERSION)))
                .networkProtocolVersion(() -> String.valueOf(VERSION))
                .simpleChannel();

        // TODO: Using "markAsLoginPacket" seems like the correct solution, but there is no way to
        //  reply to message, thus the client is unable to login.
        //  Seems like calling FMLHandshakeHandler.handleIndexedMessage would fix this, but it is
        //  package-private.
        channel.messageBuilder(SyncTraitsPacket.class, 1)
                .decoder(SyncTraitsPacket::fromBytes)
                .encoder(SyncTraitsPacket::toBytes)
                .consumer(TraitManager::handleTraitSyncPacket)
//                .markAsLoginPacket()
                .add();
        channel.messageBuilder(SyncGearPartsPacket.class, 2)
                .decoder(SyncGearPartsPacket::fromBytes)
                .encoder(SyncGearPartsPacket::toBytes)
                .consumer(PartManager::handlePartSyncPacket)
//                .markAsLoginPacket()
                .add();
    }

    public static void init() { }
}

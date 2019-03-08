package net.silentchaos512.gear.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.gear.SilentGear;

import java.util.Objects;

public class Network {
    private static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "network");

    public static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
                .clientAcceptedVersions(s -> Objects.equals(s, "1"))
                .serverAcceptedVersions(s -> Objects.equals(s, "1"))
                .networkProtocolVersion(() -> "1")
                .simpleChannel();

//        channel.messageBuilder(ClientSyncMessage.class, 1)
//                .decoder(ClientSyncMessage::fromBytes)
//                .encoder(ClientSyncMessage::toBytes)
//                .consumer(ClientHandler::onMessage)
//                .add();
//        channel.messageBuilder(ClientLoginMessage.class, 2)
//                .decoder(ClientLoginMessage::fromBytes)
//                .encoder(ClientLoginMessage::toBytes)
//                .consumer(ClientHandler::onLoginMessage)
//                .add();
    }
}

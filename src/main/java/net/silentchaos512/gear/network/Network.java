package net.silentchaos512.gear.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitManager;

import java.util.Objects;

public final class Network {
    private static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "network");
    private static final String VERSION = "sgear-net4";

    public static SimpleChannel channel;

    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
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
        channel.messageBuilder(ShowPartsScreenPacket.class, 5)
                .encoder((packet, buffer) -> {})
                .decoder(buffer -> new ShowPartsScreenPacket())
                .consumer(ShowPartsScreenPacket::handle)
                .add();
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
    }

    private Network() {}

    public static void init() {}
}

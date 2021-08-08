package net.silentchaos512.gear.network;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

// This exists solely because Block#getTranslatedName is client-only... darn it Mojang
public class ProspectingResultPacket {
    private final Set<BlockState> blocksFound = new HashSet<>();

    public ProspectingResultPacket(Set<BlockState> blocksFound) {
        this.blocksFound.addAll(blocksFound);
    }

    public static ProspectingResultPacket decode(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        Set<BlockState> blocks = new HashSet<>();

        for (int i = 0; i < count; ++i) {
            Block block = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
            if (block != null) {
                blocks.add(block.defaultBlockState());
            }
        }

        return new ProspectingResultPacket(blocks);
    }

    public static void encode(ProspectingResultPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.blocksFound.size());
        packet.blocksFound.forEach(block -> buffer.writeResourceLocation(NameUtils.from(block.getBlock())));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Component text = this.blocksFound.stream()
                    .map(state -> state.getBlock().getName())
                    .reduce((t1, t2) -> t1.append(", ").append(t2))
                    .orElseGet(() -> TextUtil.translate("item", "prospector_hammer.no_finds"));
            player.sendMessage(!this.blocksFound.isEmpty() ? TextUtil.translate("item", "prospector_hammer.finds", text) : text, Util.NIL_UUID);
        }

        context.get().setPacketHandled(true);
    }
}

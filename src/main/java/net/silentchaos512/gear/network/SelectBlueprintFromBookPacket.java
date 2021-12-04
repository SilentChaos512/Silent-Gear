package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookItem;

import java.util.function.Supplier;

public class SelectBlueprintFromBookPacket {
    private final int bookSlot;
    private final int slot;

    public SelectBlueprintFromBookPacket(int bookSlot, int slot) {
        this.bookSlot = bookSlot;
        this.slot = slot;
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null && player.containerMenu != null && this.bookSlot >= 0 && this.bookSlot < player.containerMenu.slots.size()) {
            ItemStack book = player.containerMenu.getSlot(this.bookSlot).getItem();

            if (!book.isEmpty() && book.getItem() instanceof BlueprintBookItem) {
                BlueprintBookItem.setSelectedSlot(book, this.slot);
            }
        }

        context.get().setPacketHandled(true);
    }

    public static SelectBlueprintFromBookPacket decode(FriendlyByteBuf buffer) {
        int bookSlot = buffer.readVarInt();
        int slot = buffer.readVarInt();
        return new SelectBlueprintFromBookPacket(bookSlot, slot);
    }

    public static void encode(SelectBlueprintFromBookPacket msg, FriendlyByteBuf buffer) {
        buffer.writeVarInt(msg.bookSlot);
        buffer.writeVarInt(msg.slot);
    }
}

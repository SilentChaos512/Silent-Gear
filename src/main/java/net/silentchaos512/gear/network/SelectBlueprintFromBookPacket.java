package net.silentchaos512.gear.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
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
        ServerPlayerEntity player = context.get().getSender();
        if (player != null && player.openContainer != null && this.bookSlot >= 0 && this.bookSlot < player.openContainer.inventorySlots.size()) {
            ItemStack book = player.openContainer.getSlot(this.bookSlot).getStack();

            if (!book.isEmpty() && book.getItem() instanceof BlueprintBookItem) {
                BlueprintBookItem.setSelectedSlot(book, this.slot);
            }
        }

        context.get().setPacketHandled(true);
    }

    public static SelectBlueprintFromBookPacket decode(PacketBuffer buffer) {
        int bookSlot = buffer.readVarInt();
        int slot = buffer.readVarInt();
        return new SelectBlueprintFromBookPacket(bookSlot, slot);
    }

    public static void encode(SelectBlueprintFromBookPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.bookSlot);
        buffer.writeVarInt(msg.slot);
    }
}

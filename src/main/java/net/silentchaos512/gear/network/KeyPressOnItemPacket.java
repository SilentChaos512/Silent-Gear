package net.silentchaos512.gear.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.item.ICycleItem;

import java.util.function.Supplier;

public class KeyPressOnItemPacket {
    private final Type type;
    private final int slot;

    public KeyPressOnItemPacket(Type type, int slot) {
        this.type = type;
        this.slot = slot;
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = context.get().getSender();
        if (player != null && player.openContainer != null && this.slot >= 0 && this.slot < player.openContainer.inventorySlots.size()) {
            Slot inventorySlot = player.openContainer.getSlot(this.slot);
            ItemStack stack = inventorySlot.getStack();

            if (!stack.isEmpty()) {
                switch (this.type) {
                    case CYCLE_BACK:
                    case CYCLE_NEXT:
                        if (stack.getItem() instanceof ICycleItem) {
                            ((ICycleItem) stack.getItem()).onCycleKeyPress(stack, this.type);
                            // Update crafting grids
                            player.openContainer.onCraftMatrixChanged(inventorySlot.inventory);
                        }
                        break;
                    case OPEN_ITEM:
                        // TODO
                        break;
                }
            }
        }

        context.get().setPacketHandled(true);
    }

    public static KeyPressOnItemPacket decode(PacketBuffer buffer) {
        Type type = buffer.readEnumValue(Type.class);
        int slot = buffer.readVarInt();
        return new KeyPressOnItemPacket(type, slot);
    }

    public static void encode(KeyPressOnItemPacket msg, PacketBuffer buffer) {
        buffer.writeEnumValue(msg.type);
        buffer.writeVarInt(msg.slot);
    }

    public enum Type {
        CYCLE_BACK(-1), CYCLE_NEXT(1), OPEN_ITEM(0);

        public final int direction;

        Type(int direction) {
            this.direction = direction;
        }
    }
}

package net.silentchaos512.gear.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
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
        ServerPlayer player = context.get().getSender();
        if (player != null && player.containerMenu != null && this.slot >= 0 && this.slot < player.containerMenu.slots.size()) {
            Slot inventorySlot = player.containerMenu.getSlot(this.slot);
            ItemStack stack = inventorySlot.getItem();

            if (!stack.isEmpty()) {
                switch (this.type) {
                    case CYCLE_BACK:
                    case CYCLE_NEXT:
                        if (stack.getItem() instanceof ICycleItem) {
                            ((ICycleItem) stack.getItem()).onCycleKeyPress(stack, this.type);
                            // Update crafting grids
                            player.containerMenu.slotsChanged(inventorySlot.container);
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

    public static KeyPressOnItemPacket decode(FriendlyByteBuf buffer) {
        Type type = buffer.readEnum(Type.class);
        int slot = buffer.readVarInt();
        return new KeyPressOnItemPacket(type, slot);
    }

    public static void encode(KeyPressOnItemPacket msg, FriendlyByteBuf buffer) {
        buffer.writeEnum(msg.type);
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

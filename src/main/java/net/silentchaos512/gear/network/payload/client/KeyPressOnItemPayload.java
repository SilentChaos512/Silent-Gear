package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.ICycleItem;

public record KeyPressOnItemPayload(Type type, int slot) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("key_press_on_item");

    public KeyPressOnItemPayload(FriendlyByteBuf buf) {
        this(buf.readEnum(Type.class), buf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(type);
        pBuffer.writeVarInt(slot);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public enum Type {
        CYCLE_BACK(ICycleItem.Direction.BACK),
        CYCLE_NEXT(ICycleItem.Direction.NEXT),
        OPEN_ITEM(ICycleItem.Direction.NEITHER);

        public final ICycleItem.Direction direction;

        Type(ICycleItem.Direction direction) {
            this.direction = direction;
        }
    }
}

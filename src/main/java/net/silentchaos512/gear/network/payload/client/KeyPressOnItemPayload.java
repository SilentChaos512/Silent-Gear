package net.silentchaos512.gear.network.payload.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.ICycleItem;

public record KeyPressOnItemPayload(KeyPressType keyPressType, int slot) implements CustomPacketPayload {
    public static final Type<KeyPressOnItemPayload> TYPE = new Type<>(SilentGear.getId("key_press_on_item"));

    public static final StreamCodec<FriendlyByteBuf, KeyPressOnItemPayload> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                buf.writeVarInt(data.keyPressType.ordinal());
                buf.writeVarInt(data.slot);
            },
            buf -> {
                var keyType = KeyPressType.values()[buf.readVarInt()];
                var slot = buf.readVarInt();
                return new KeyPressOnItemPayload(keyType, slot);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum KeyPressType {
        CYCLE_BACK(ICycleItem.Direction.BACK),
        CYCLE_NEXT(ICycleItem.Direction.NEXT),
        OPEN_ITEM(ICycleItem.Direction.NEITHER);

        public final ICycleItem.Direction direction;

        KeyPressType(ICycleItem.Direction direction) {
            this.direction = direction;
        }
    }
}

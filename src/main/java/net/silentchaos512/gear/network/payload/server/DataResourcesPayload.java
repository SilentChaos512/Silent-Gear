package net.silentchaos512.gear.network.payload.server;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public interface DataResourcesPayload<T> {
    Map<Identifier, T> values();

    /**
     * Creates a map stream codec which reports the key of any value that fails to encode or decode.
     * Without this, a single bad data pack entry produces a generic packet error with no hint
     * about which file caused it. Uses the same wire format as {@link net.minecraft.network.codec.ByteBufCodecs#map}.
     *
     * @param typeName   Name of the value type, used in error messages (e.g. "material")
     * @param valueCodec Stream codec for the values
     * @param <V>        The value type
     * @return A stream codec for maps of values
     */
    static <V> StreamCodec<RegistryFriendlyByteBuf, HashMap<Identifier, V>> mapStreamCodec(
            String typeName,
            StreamCodec<RegistryFriendlyByteBuf, V> valueCodec
    ) {
        return StreamCodec.of(
                (buf, map) -> {
                    buf.writeVarInt(map.size());
                    map.forEach((id, value) -> {
                        Identifier.STREAM_CODEC.encode(buf, id);
                        try {
                            valueCodec.encode(buf, value);
                        } catch (Exception ex) {
                            throw new EncoderException("Failed to encode Silent Gear " + typeName + " \"" + id + "\"", ex);
                        }
                    });
                },
                buf -> {
                    int size = buf.readVarInt();
                    HashMap<Identifier, V> map = HashMap.newHashMap(size);
                    for (int i = 0; i < size; ++i) {
                        Identifier id = Identifier.STREAM_CODEC.decode(buf);
                        try {
                            map.put(id, valueCodec.decode(buf));
                        } catch (Exception ex) {
                            throw new DecoderException("Failed to decode Silent Gear " + typeName + " \"" + id
                                    + "\" sent by the server. Check the " + typeName + "'s JSON file for errors.", ex);
                        }
                    }
                    return map;
                }
        );
    }
}

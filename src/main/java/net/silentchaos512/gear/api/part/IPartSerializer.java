package net.silentchaos512.gear.api.part;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.gear.part.AbstractGearPart;

/**
 * Used for loading parts from JSON, and for encoding/decoding the information sent from the server
 * to clients.
 * <p>
 * In most cases, {@link AbstractGearPart.Serializer} should be used. But if you do need something
 * more specific, implement this.
 *
 * @param <T> The part class
 */
public interface IPartSerializer<T extends IGearPart> {
    /**
     * Loads the part from JSON.
     * <p>
     * NOTE: If objects are missing from {@code json}, a {@link com.google.gson.JsonSyntaxException}
     * should be thrown. This helps detect malformed JSON.
     *
     * @param id   The part ID
     * @param json The JSON from the part file
     * @return A new part
     */
    T read(ResourceLocation id, JsonObject json);

    /**
     * Reads the part from a server-to-client sync packet. This is necessary for clients to receive
     * parts when connecting to a dedicated server, but the packet is also received when playing
     * singleplayer, overwriting the objects created in {@link #read(ResourceLocation, JsonObject)}.
     * You can use {@link IGearPart#retainData(IGearPart)} to keep information not needed by the
     * client.
     *
     * @param id     The part ID
     * @param buffer The PacketBuffer
     * @return A new part
     */
    T read(ResourceLocation id, FriendlyByteBuf buffer);

    /**
     * Writes the part for a server-to-client sync packet. All written information should be read in
     * {@link #read(ResourceLocation, PacketBuffer)}. You should only write data that the client
     * needs to know to conserve bandwidth. All other information should be copied in {@link
     * IGearPart#retainData(IGearPart)}.
     *
     * @param buffer The PacketBuffer
     * @param part   The gear part
     */
    void write(FriendlyByteBuf buffer, T part);

    /**
     * A unique ID for the serializer.
     *
     * @return The serializer ID
     */
    ResourceLocation getName();
}

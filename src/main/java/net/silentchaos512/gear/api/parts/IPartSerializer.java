package net.silentchaos512.gear.api.parts;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * Used for loading parts from JSON, and for encoding/decoding the information sent from the server
 * to clients. Very similar to {@link net.minecraft.item.crafting.IRecipeSerializer}.
 *
 * @param <T> The part class
 */
public interface IPartSerializer<T extends IGearPart> {
    T read(ResourceLocation id, JsonObject json);

    T read(ResourceLocation id, PacketBuffer buffer);

    void write(PacketBuffer buffer, T part);

    ResourceLocation getName();
}

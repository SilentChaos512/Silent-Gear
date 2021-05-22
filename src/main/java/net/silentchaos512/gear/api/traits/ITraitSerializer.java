package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface ITraitSerializer<T extends ITrait> {
    T read(ResourceLocation id, JsonObject json); // TODO: rename to deserializeJson?

    T read(ResourceLocation id, PacketBuffer buffer); // TODO: rename to readFromNetwork?

    void write(PacketBuffer buffer, T trait); // TODO: rename to writeToNetwork?

    ResourceLocation getName();
}

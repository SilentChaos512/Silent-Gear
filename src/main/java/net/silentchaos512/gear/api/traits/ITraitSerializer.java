package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface ITraitSerializer<T extends ITrait> {
    T read(ResourceLocation id, JsonObject json);

    T read(ResourceLocation id, PacketBuffer buffer);

    void write(PacketBuffer buffer, T trait);

    ResourceLocation getName();
}

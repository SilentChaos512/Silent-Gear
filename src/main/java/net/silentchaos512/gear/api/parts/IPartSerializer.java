package net.silentchaos512.gear.api.parts;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IPartSerializer<T extends IGearPart> {
    T read(ResourceLocation id, JsonObject json);

    T read(ResourceLocation id, PacketBuffer buffer);

    void write(PacketBuffer buffer, T part);

    ResourceLocation getName();
}

package net.silentchaos512.gear.api.material;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IMaterialSerializer<T extends IMaterial> {
    T deserialize(ResourceLocation id, String packName, JsonObject json);

    T read(ResourceLocation id, PacketBuffer buffer);

    void write(PacketBuffer buffer, T material);

    ResourceLocation getName();
}

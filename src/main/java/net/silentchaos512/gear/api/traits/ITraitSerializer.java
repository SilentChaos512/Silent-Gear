package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ITraitSerializer<T extends ITrait> {
    T read(ResourceLocation id, JsonObject json); // TODO: rename to deserializeJson?

    T read(ResourceLocation id, FriendlyByteBuf buffer); // TODO: rename to readFromNetwork?

    void write(FriendlyByteBuf buffer, T trait); // TODO: rename to writeToNetwork?

    ResourceLocation getName();
}

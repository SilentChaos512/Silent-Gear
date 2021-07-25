package net.silentchaos512.gear.api.material;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Handles serialization of {@link IMaterial}s. Custom serializers <em>should not be required in
 * most cases.</em>
 *
 * @param <T> The material class
 */
public interface IMaterialSerializer<T extends IMaterial> {
    T deserialize(ResourceLocation id, String packName, JsonObject json);

    T read(ResourceLocation id, FriendlyByteBuf buffer);

    void write(FriendlyByteBuf buffer, T material);

    ResourceLocation getName();
}

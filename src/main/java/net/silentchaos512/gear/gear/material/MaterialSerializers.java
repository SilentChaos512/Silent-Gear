package net.silentchaos512.gear.gear.material;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialSerializer;

import java.util.HashMap;
import java.util.Map;

public final class MaterialSerializers {
    private static final Map<ResourceLocation, IMaterialSerializer<?>> REGISTRY = new HashMap<>();
    public static final PartMaterial.Serializer STANDARD = register(new PartMaterial.Serializer());
    public static final CompoundMaterial.Serializer COMPOUND = register(new CompoundMaterial.Serializer());

    private MaterialSerializers() {}

    public static <S extends IMaterialSerializer<T>, T extends IMaterial> S register(S serializer) {
        if (REGISTRY.containsKey(serializer.getName())) {
            throw new IllegalArgumentException("Duplicate material serializer " + serializer.getName());
        }
        SilentGear.LOGGER.info(MaterialManager.MARKER, "Registered material serializer '{}'", serializer.getName());
        REGISTRY.put(serializer.getName(), serializer);
        return serializer;
    }

    public static IMaterial deserialize(ResourceLocation id, String packName, JsonObject json) {
        ResourceLocation type = SilentGear.getIdWithDefaultNamespace(JSONUtils.getString(json, "type", STANDARD.getName().toString()));
        IMaterialSerializer<?> serializer = REGISTRY.getOrDefault(type, STANDARD);
        return serializer.deserialize(id, packName, json);
    }

    public static IMaterial read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        ResourceLocation type = buffer.readResourceLocation();
        IMaterialSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown material serializer: " + type);
        }
        return serializer.read(id, buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IMaterial> void write(T material, PacketBuffer buffer) {
        buffer.writeResourceLocation(material.getId());
        buffer.writeResourceLocation(material.getSerializer().getName());
        IMaterialSerializer<T> serializer = (IMaterialSerializer<T>) material.getSerializer();
        serializer.write(buffer, material);
    }
}

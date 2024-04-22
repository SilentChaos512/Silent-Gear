package net.silentchaos512.gear.gear.material;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MaterialSerializers {
    private static final Map<ResourceLocation, IMaterialSerializer<?>> REGISTRY = new HashMap<>();

    public static final AbstractMaterial.Serializer<PartMaterial> STANDARD = register( // TODO: Rename to silentgear:simple?
            new AbstractMaterial.Serializer<>(SilentGear.getId("standard"), PartMaterial::new));
    public static final CompoundMaterial.Serializer COMPOUND = register(
            new CompoundMaterial.Serializer());
    public static final AbstractMaterial.Serializer<CustomCompoundMaterial> CUSTOM_COMPOUND = register(
            new AbstractMaterial.Serializer<>(SilentGear.getId("custom_compound"), CustomCompoundMaterial::new));
    public static final CraftedMaterial.Serializer CRAFTED = register(
            new CraftedMaterial.Serializer(SilentGear.getId("crafted")));

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
        ResourceLocation type = SilentGear.getIdWithDefaultNamespace(GsonHelper.getAsString(json, "type", STANDARD.getName().toString()));
        IMaterialSerializer<?> serializer = REGISTRY.getOrDefault(type, STANDARD);
        return serializer.deserialize(id, packName, json);
    }

    public static IMaterial read(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        ResourceLocation type = buffer.readResourceLocation();
        IMaterialSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown material serializer: " + type);
        }
        return serializer.read(id, buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IMaterial> void write(T material, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(material.getId());
        buffer.writeResourceLocation(material.getSerializer().getName());
        IMaterialSerializer<T> serializer = (IMaterialSerializer<T>) material.getSerializer();
        serializer.write(buffer, material);
    }

    public static List<IMaterial> readAll(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<IMaterial> ret = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ret.add(read(buf));
        }
        return ImmutableList.copyOf(ret);
    }

    public static void writeAll(List<IMaterial> materials, FriendlyByteBuf buf) {
        buf.writeVarInt(materials.size());
        for (IMaterial material : materials) {
            write(material, buf);
        }
    }
}

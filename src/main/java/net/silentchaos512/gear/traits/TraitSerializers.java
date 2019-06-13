package net.silentchaos512.gear.traits;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;

import java.util.HashMap;
import java.util.Map;

public final class TraitSerializers {
    private static final Map<ResourceLocation, ITraitSerializer<?>> REGISTRY = new HashMap<>();

    static {
        register(SimpleTrait.SERIALIZER);
        register(DamageTypeTrait.SERIALIZER);
        register(DurabilityTrait.SERIALIZER);
        register(EnchantmentTrait.SERIALIZER);
        register(PotionEffectTrait.SERIALIZER);
        register(StatModifierTrait.SERIALIZER);
    }

    private TraitSerializers() {}

    public static <S extends ITraitSerializer<T>, T extends ITrait> S register(S serializer) {
        if (REGISTRY.containsKey(serializer.getName())) {
            throw new IllegalArgumentException("Duplicate trait serializer " + serializer.getName());
        }
        REGISTRY.put(serializer.getName(), serializer);
        return serializer;
    }

    public static ITrait deserialize(ResourceLocation id, JsonObject json) {
        String typeStr = JSONUtils.getString(json, "type");
        if (!typeStr.contains(":")) typeStr = SilentGear.RESOURCE_PREFIX + typeStr;
        ResourceLocation type = new ResourceLocation(typeStr);

        ITraitSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new JsonParseException("Invalid or unsupported trait type " + type);
        }
        return serializer.read(id, json);
    }

    public static ITrait read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        ResourceLocation type = buffer.readResourceLocation();
        ITraitSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown trait serializer " + type);
        }
        return serializer.read(id, buffer);
    }

    public static <T extends ITrait> void write(T trait, PacketBuffer buffer) {
        buffer.writeResourceLocation(trait.getId());
        buffer.writeResourceLocation(trait.getSerializer().getName());
        ITraitSerializer<T> serializer = (ITraitSerializer<T>) trait.getSerializer();
        serializer.write(buffer, trait);
    }
}

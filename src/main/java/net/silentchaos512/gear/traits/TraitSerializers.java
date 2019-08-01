package net.silentchaos512.gear.traits;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.config.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TraitSerializers {
    private static final Map<ResourceLocation, ITraitSerializer<?>> REGISTRY = new HashMap<>();

    static {
        register(SimpleTrait.SERIALIZER);
        register(DamageTypeTrait.SERIALIZER);
        register(DurabilityTrait.SERIALIZER);
        register(EnchantmentTrait.SERIALIZER);
        register(NBTTrait.SERIALIZER);
        register(PotionEffectTrait.SERIALIZER);
        register(StatModifierTrait.SERIALIZER);
    }

    private TraitSerializers() {}

    public static <S extends ITraitSerializer<T>, T extends ITrait> S register(S serializer) {
        if (REGISTRY.containsKey(serializer.getName())) {
            throw new IllegalArgumentException("Duplicate trait serializer " + serializer.getName());
        }
        SilentGear.LOGGER.info(TraitManager.MARKER, "Registered serializer '{}'", serializer.getName());
        REGISTRY.put(serializer.getName(), serializer);
        return serializer;
    }

    public static ITrait deserialize(ResourceLocation id, JsonObject json) {
        String typeStr = JSONUtils.getString(json, "type");
        if (!typeStr.contains(":")) typeStr = SilentGear.RESOURCE_PREFIX + typeStr;
        ResourceLocation type = new ResourceLocation(typeStr);
        log(() -> "deserialize " + id + " (type " + type + ")");

        ITraitSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new JsonParseException("Invalid or unsupported trait type " + type);
        }
        return serializer.read(id, json);
    }

    public static ITrait read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        ResourceLocation type = buffer.readResourceLocation();
        log(() -> "read " + id + " (type " + type + ")");
        ITraitSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown trait serializer " + type);
        }
        return serializer.read(id, buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ITrait> void write(T trait, PacketBuffer buffer) {
        ResourceLocation id = trait.getId();
        ResourceLocation type = trait.getSerializer().getName();
        log(() -> "write " + id + " (type " + type + ")");
        buffer.writeResourceLocation(id);
        buffer.writeResourceLocation(type);
        ITraitSerializer<T> serializer = (ITraitSerializer<T>) trait.getSerializer();
        serializer.write(buffer, trait);
    }

    private static void log(Supplier<?> msg) {
        if (Config.GENERAL.extraPartAndTraitLogging.get()) {
            SilentGear.LOGGER.info(TraitManager.MARKER, msg.get());
        }
    }
}

package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.trait.condition.*;

import java.util.*;
import java.util.function.Supplier;

public final class TraitSerializers {
    // TODO: Change to Forge registry?
    private static final Map<ResourceLocation, ITraitSerializer<?>> REGISTRY = new HashMap<>();

    static {
        register(SimpleTrait.SERIALIZER);
        register(DamageTypeTrait.SERIALIZER);
        register(DurabilityTraitEffect.SERIALIZER);
        register(EnchantmentTrait.SERIALIZER);
        register(NBTTrait.SERIALIZER);
        register(WielderEffectTrait.SERIALIZER);
        register(StatModifierTrait.SERIALIZER);
        register(AttributeTraitEffect.SERIALIZER);
        register(BlockPlacerTrait.SERIALIZER);
        register(BlockFillerTraitEffect.SERIALIZER);
        register(SynergyTrait.SERIALIZER);
        register(TargetEffectTrait.SERIALIZER);
        register(BonusDropsTraitEffect.SERIALIZER);
        register(CancelEffectsTrait.SERIALIZER);
        register(SelfRepairTrait.SERIALIZER);
        register(StellarTrait.SERIALIZER);
        register(BlockMiningSpeedTrait.SERIALIZER);
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

    public static ITraitCondition deserializeCondition(JsonObject json) {
        ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(json, "type"));
        ITraitConditionSerializer<?> serializer = CONDITIONS.get(type);
        if (serializer == null) {
            throw new JsonSyntaxException("Unknown trait condition type: " + type);
        }
        return serializer.deserialize(json);
    }

    public static ITrait deserialize(ResourceLocation id, JsonObject json) {
        String typeStr = GsonHelper.getAsString(json, "type");
        ResourceLocation type = SilentGear.getIdWithDefaultNamespace(typeStr);
        log(() -> "deserialize " + id + " (type " + type + ")");

        ITraitSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new JsonSyntaxException("Invalid or unsupported trait type " + type);
        }
        return serializer.read(id, json);
    }

    public static ITrait read(FriendlyByteBuf buffer) {
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
    public static <T extends ITrait> void write(T trait, FriendlyByteBuf buffer) {
        ResourceLocation id = trait.getId();
        ResourceLocation type = trait.getSerializer().getName();
        log(() -> "write " + id + " (type " + type + ")");
        buffer.writeResourceLocation(id);
        buffer.writeResourceLocation(type);
        ITraitSerializer<T> serializer = (ITraitSerializer<T>) trait.getSerializer();
        serializer.write(buffer, trait);
    }

    public static List<ITrait> readAll(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<ITrait> ret = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ret.add(read(buf));
        }
        return ImmutableList.copyOf(ret);
    }

    public static void writeAll(List<ITrait> traits, FriendlyByteBuf buf) {
        buf.writeVarInt(traits.size());
        for (ITrait trait : traits) {
            write(trait, buf);
        }
    }

    private static void log(Supplier<?> msg) {
        if (Config.Common.extraPartAndTraitLogging.get()) {
            SilentGear.LOGGER.info(TraitManager.MARKER, msg.get());
        }
    }

    public static Collection<ITraitSerializer<?>> getSerializers() {
        return REGISTRY.values();
    }
}

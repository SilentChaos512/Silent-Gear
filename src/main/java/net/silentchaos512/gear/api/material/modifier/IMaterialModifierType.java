package net.silentchaos512.gear.api.material.modifier;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;

import javax.annotation.Nullable;

public interface IMaterialModifierType<T extends IMaterialModifier> {
    ResourceLocation getId();

    void removeModifier(ItemStack stack);

    @Nullable
    T read(CompoundTag tag);

    default T read(IMaterialInstance material) {
        return read(material.getItem().getOrCreateTag());
    }

    void write(T modifier, CompoundTag tag);

    default void write(T modifier, ItemStack stack) {
        write(modifier, stack.getOrCreateTag());
    }

    T readFromNetwork(FriendlyByteBuf buf);

    void writeToNetwork(T modifier, FriendlyByteBuf buf);

    T deserialize(JsonObject json);

    JsonObject serialize(T modifier);

    Codec<T> codec();
}

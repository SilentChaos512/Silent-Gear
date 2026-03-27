package net.silentchaos512.gear.api.material.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IMaterialModifierType<T extends IMaterialModifier> {
    Identifier getId();

    Optional<T> readModifier(ItemInstance instance);

    void addModifier(T mod, ItemStack stack);

    void removeModifier(ItemStack stack);

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}

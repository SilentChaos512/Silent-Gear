package net.silentchaos512.gear.api.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IMaterialInstance {
    ResourceLocation getMaterialId();

    @Nullable
    IPartMaterial getMaterial();

    ItemStack getItem();

    CompoundNBT write(CompoundNBT nbt);
}

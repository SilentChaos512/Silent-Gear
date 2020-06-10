package net.silentchaos512.gear.api.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.MaterialGrade;

import javax.annotation.Nullable;

public interface IMaterialInstance {
    ResourceLocation getMaterialId();

    @Nullable
    IMaterial getMaterial();

    MaterialGrade getGrade();

    ItemStack getItem();

    CompoundNBT write(CompoundNBT nbt);
}

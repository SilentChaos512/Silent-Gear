package net.silentchaos512.gear.api.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartType;

import javax.annotation.Nullable;

public interface IMaterialInstance {
    ResourceLocation getMaterialId();

    @Nullable
    IMaterial getMaterial();

    MaterialGrade getGrade();

    ItemStack getItem();

    CompoundNBT write(CompoundNBT nbt);

    int getColor(PartType partType, ItemStack gear);

    default int getColor(PartType partType) {
        return getColor(partType, ItemStack.EMPTY);
    }

    ITextComponent getDisplayName(PartType partType, ItemStack gear);

    default ITextComponent getDisplayName(PartType partType) {
        return getDisplayName(partType, ItemStack.EMPTY);
    }
}

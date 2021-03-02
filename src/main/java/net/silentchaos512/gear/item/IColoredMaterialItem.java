package net.silentchaos512.gear.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;

public interface IColoredMaterialItem {
    String NBT_MATERIALS = "Materials";

    @Nullable
    default MaterialInstance getPrimarySubMaterial(ItemStack stack) {
        ListNBT listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        for (INBT nbt : listNbt) {
            IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(nbt.getString()));
            if (mat != null) {
                return MaterialInstance.of(mat);
            }
        }
        return null;
    }

    int getColor(ItemStack stack, int layer);
}

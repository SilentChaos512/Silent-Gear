package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;

public interface IColoredMaterialItem {
    String NBT_MATERIALS = "Materials";

    @Nullable
    default IMaterialInstance getPrimarySubMaterial(ItemStack stack) {
        ListTag listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Constants.NBT.TAG_STRING);
        for (Tag nbt : listNbt) {
            IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(nbt.getAsString()));
            if (mat != null) {
                return MaterialInstance.of(mat);
            }
        }
        return null;
    }

    int getColor(ItemStack stack, int layer);
}

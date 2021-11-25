package net.silentchaos512.gear.api.material.modifier;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;

import javax.annotation.Nullable;

public interface IMaterialModifierType {
    void removeModifier(ItemStack stack);

    @Nullable IMaterialModifier read(IMaterialInstance material);
}

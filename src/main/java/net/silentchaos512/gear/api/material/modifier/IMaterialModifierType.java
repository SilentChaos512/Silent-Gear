package net.silentchaos512.gear.api.material.modifier;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;

import javax.annotation.Nullable;

public interface IMaterialModifierType<T extends IMaterialModifier> {
    void removeModifier(ItemStack stack);

    @Nullable IMaterialModifier read(IMaterialInstance material);

    void write(T modifier, ItemStack stack);
}

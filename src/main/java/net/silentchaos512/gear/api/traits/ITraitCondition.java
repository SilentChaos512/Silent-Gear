package net.silentchaos512.gear.api.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.PartDataList;

public interface ITraitCondition {
    ResourceLocation getId();

    boolean matches(ItemStack gear, PartDataList parts, ITrait trait);
}

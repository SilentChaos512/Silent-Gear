package net.silentchaos512.gear.api.traits;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.parts.PartDataList;

@FunctionalInterface
public interface ITraitCondition {
    boolean matches(ItemStack gear, PartDataList parts, ITrait trait);
}

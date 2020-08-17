package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

public interface IBlueprint {
    PartType getPartType(ItemStack stack);

    GearType getGearType(ItemStack stack);
}

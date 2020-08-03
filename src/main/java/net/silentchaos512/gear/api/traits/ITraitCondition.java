package net.silentchaos512.gear.api.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.List;

public interface ITraitCondition {
    ResourceLocation getId();

    boolean matches(ItemStack gear, PartDataList parts, ITrait trait);

    boolean matches(ItemStack gear, PartType partType, List<MaterialInstance> materials, ITrait trait);
}

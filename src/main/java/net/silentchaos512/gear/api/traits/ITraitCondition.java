package net.silentchaos512.gear.api.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.List;

public interface ITraitCondition {
    ResourceLocation getId();

    ITraitConditionSerializer<?> getSerializer();

    boolean matches(ItemStack gear, PartDataList parts, ITrait trait);

    boolean matches(ItemStack gear, PartType partType, List<MaterialInstance> materials, ITrait trait);

    IFormattableTextComponent getDisplayText();
}

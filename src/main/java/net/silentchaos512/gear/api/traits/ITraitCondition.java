package net.silentchaos512.gear.api.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.List;

public interface ITraitCondition {
    ResourceLocation getId();

    ITraitConditionSerializer<?> getSerializer();

    @Deprecated
    boolean matches(ItemStack gear, GearType gearType, PartDataList parts, ITrait trait);

    @Deprecated
    boolean matches(ItemStack gear, GearType gearType, PartType partType, List<MaterialInstance> materials, ITrait trait);

    boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<IGearComponentInstance<?>> components);

    IFormattableTextComponent getDisplayText();
}

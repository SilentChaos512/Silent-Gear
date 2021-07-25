package net.silentchaos512.gear.api.traits;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;

import java.util.List;

public interface ITraitCondition {
    ResourceLocation getId();

    ITraitConditionSerializer<?> getSerializer();

    boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<? extends IGearComponentInstance<?>> components);

    MutableComponent getDisplayText();
}

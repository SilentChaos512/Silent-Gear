package net.silentchaos512.gear.api.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartType;

import javax.annotation.Nullable;

public interface IGearComponent<D> extends IStatModProvider<D>, ITraitProvider<D> {
    /**
     * Gets the ingredient to match for crafting.
     *
     * @return The ingredient to match
     */
    Ingredient getIngredient();

    /**
     * Gets the materials this object was made with, assuming it is a compound part/material.
     *
     * @param instance The object (part, material)
     * @return List of materials used to craft this object, or an empty list if not applicable
     */
    MaterialList getMaterials(D instance);

    /**
     * Determine if this component can be used to craft parts of a given type and for a given gear
     * type.
     *
     * @param instance  The object (part, material)
     * @param partType  The part type
     * @param gearType  The gear type
     * @param inventory The inventory the item is in (crafting grid, etc.)
     * @return True if and only if crafting should be allowed
     */
    boolean isCraftingAllowed(D instance, PartType partType, GearType gearType, @Nullable Container inventory);

    default boolean isCraftingAllowed(D instance, PartType partType, GearType gearType) {
        return isCraftingAllowed(instance, partType, gearType, null);
    }

    Component getDisplayName(@Nullable D instance, PartType type, ItemStack gear);
}

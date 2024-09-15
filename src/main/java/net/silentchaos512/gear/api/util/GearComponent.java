package net.silentchaos512.gear.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;

import javax.annotation.Nullable;
import java.util.Collection;

public interface GearComponent<D> extends PropertyProvider<D> {
    /**
     * Gets the ingredient to match for crafting.
     *
     * @return The ingredient to match
     */
    Ingredient getIngredient();

    /**
     * Determine if this component can be used to craft parts of a given type and for a given gear
     * type.
     *
     * @param instance      The object (part, material)
     * @param partType      The part type
     * @param gearType      The gear type
     * @param craftingInput The inventory the item is in (crafting grid, etc.)
     * @return True if and only if crafting should be allowed
     */
    boolean isCraftingAllowed(D instance, PartType partType, GearType gearType, @Nullable CraftingInput craftingInput);

    default boolean isCraftingAllowed(D instance, PartType partType, GearType gearType) {
        return isCraftingAllowed(instance, partType, gearType, null);
    }

    Component getDisplayName(@Nullable D instance, PartType type);
}

package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.lib.crafting.recipe.ExtendedSingleItemRecipe;

public class PressingRecipe extends ExtendedSingleItemRecipe {
    public PressingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result) {
        super(ModRecipes.PRESSING_TYPE, ModRecipes.PRESSING.get(), id, "", ingredient, result);
    }
}

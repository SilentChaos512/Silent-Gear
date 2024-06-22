package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.SgRecipes;

public class PressingRecipe extends SingleItemRecipe {
    public PressingRecipe(String group, Ingredient ingredient, ItemStack result) {
        this(SgRecipes.PRESSING.get(), group, ingredient, result);
    }

    public PressingRecipe(RecipeSerializer<?> pSerializer, String pGroup, Ingredient pIngredient, ItemStack pResult) {
        super(SgRecipes.PRESSING_TYPE.get(), pSerializer, pGroup, pIngredient, pResult);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return ingredient.test(pContainer.getItem(0));
    }
}

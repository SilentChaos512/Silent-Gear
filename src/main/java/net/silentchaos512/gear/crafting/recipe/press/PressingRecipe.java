package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.SgRecipeBookCategories;
import net.silentchaos512.gear.setup.SgRecipes;

public class PressingRecipe extends SingleItemRecipe {
    private final RecipeSerializer<? extends SingleItemRecipe> serializer;

    public PressingRecipe(String group, Ingredient ingredient, ItemStack result) {
        this(SgRecipes.PRESSING.get(), group, ingredient, result);
    }

    public PressingRecipe(RecipeSerializer<? extends SingleItemRecipe> pSerializer, String pGroup, Ingredient pIngredient, ItemStack pResult) {
        super(pGroup, pIngredient, pResult);
        this.serializer = pSerializer;
    }

    @Override
    public RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
        return this.serializer;
    }

    @Override
    public RecipeType<? extends SingleItemRecipe> getType() {
        return SgRecipes.PRESSING_TYPE.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return SgRecipeBookCategories.METAL_PRESS.get();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input().test(input.getItem(0));
    }
}

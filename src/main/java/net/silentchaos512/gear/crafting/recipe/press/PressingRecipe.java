package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.setup.SgRecipeBookCategories;
import net.silentchaos512.gear.setup.SgRecipes;

public class PressingRecipe extends SingleItemRecipe {
    private final RecipeSerializer<? extends SingleItemRecipe> serializer;

    public PressingRecipe(CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result) {
        this(SgRecipes.PRESSING.get(), commonInfo, ingredient, result);
    }

    public PressingRecipe(RecipeSerializer<? extends SingleItemRecipe> serializer, CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result) {
        super(commonInfo, ingredient, result);
        this.serializer = serializer;
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

    @Override
    public String group() {
        return "";
    }
}

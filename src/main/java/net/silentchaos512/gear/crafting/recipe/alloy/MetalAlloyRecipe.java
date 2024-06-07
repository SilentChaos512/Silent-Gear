package net.silentchaos512.gear.crafting.recipe.alloy;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.silentchaos512.gear.setup.SgRecipes;

import java.util.List;

public class MetalAlloyRecipe extends AlloyRecipe {
    public MetalAlloyRecipe(Result result, List<Ingredient> ingredients) {
        super(result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.COMPOUNDING_METAL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SgRecipes.COMPOUNDING_METAL_TYPE.get();
    }
}

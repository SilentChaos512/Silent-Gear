package net.silentchaos512.gear.crafting.recipe.alloy;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.setup.SgRecipes;

public class MetalCompoundingRecipe extends CompoundingRecipe {
    public MetalCompoundingRecipe(ResourceLocation recipeId) {
        super();
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

package net.silentchaos512.gear.crafting.recipe.compounder;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.init.ModRecipes;

public class MetalCompoundingRecipe extends CompoundingRecipe {
    public MetalCompoundingRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COMPOUNDING_METAL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CompoundingRecipe.COMPOUNDING_METAL_TYPE;
    }
}

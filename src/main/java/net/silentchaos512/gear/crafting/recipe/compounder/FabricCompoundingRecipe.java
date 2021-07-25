package net.silentchaos512.gear.crafting.recipe.compounder;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.init.ModRecipes;

public class FabricCompoundingRecipe extends CompoundingRecipe {
    public FabricCompoundingRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COMPOUNDING_FABRIC.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COMPOUNDING_FABRIC_TYPE;
    }
}

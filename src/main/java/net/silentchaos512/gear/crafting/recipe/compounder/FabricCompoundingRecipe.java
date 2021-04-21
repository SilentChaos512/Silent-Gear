package net.silentchaos512.gear.crafting.recipe.compounder;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.init.ModRecipes;

public class FabricCompoundingRecipe extends CompoundingRecipe {
    public FabricCompoundingRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.COMPOUNDING_FABRIC.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.COMPOUNDING_FABRIC_TYPE;
    }
}

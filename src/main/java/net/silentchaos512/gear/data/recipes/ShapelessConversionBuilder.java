package net.silentchaos512.gear.data.recipes;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.crafting.recipe.ConversionRecipe;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.lib.data.recipe.ExtendedShapelessRecipeBuilder;

import java.util.List;

public class ShapelessConversionBuilder extends ExtendedShapelessRecipeBuilder<ConversionRecipe> {
    private final ICoreItem resultItem;
    private final List<PartInstance> parts;

    public ShapelessConversionBuilder(RecipeCategory category, ICoreItem result, List<PartInstance> parts) {
        super(category, result);
        this.resultItem = result;
        this.parts = parts;
    }

    @Override
    public ConversionRecipe createRecipe(ResourceLocation id) {
        return new ConversionRecipe(
                group != null ? group : "",
                RecipeBuilder.determineBookCategory(category),
                new ConversionRecipe.Result(
                        resultItem.asItem(),
                        parts
                ),
                ingredients
        );
    }
}

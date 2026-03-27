package net.silentchaos512.gear.data.recipes;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.crafting.recipe.ConversionRecipe;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.lib.data.recipe.ExtendedShapelessRecipeBuilder;

import java.util.List;

public class ShapelessConversionBuilder extends ExtendedShapelessRecipeBuilder<ConversionRecipe> {
    private final GearItem resultItem;
    private final List<PartInstance> parts;

    public ShapelessConversionBuilder(HolderGetter<Item> items, RecipeCategory category, GearItem result, List<PartInstance> parts) {
        super(items, category, result);
        this.resultItem = result;
        this.parts = parts;
    }

    @Override
    public ConversionRecipe createRecipe(ResourceKey<Recipe<?>> id) {
        return new ConversionRecipe(
                this.commonInfo,
                this.bookInfo,
                new ConversionRecipe.Result(
                        this.resultItem.asItem(),
                        this.parts
                ),
                this.ingredients
        );
    }
}

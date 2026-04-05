package net.silentchaos512.gear.data.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.crafting.recipe.ToolActionRecipe;
import org.jetbrains.annotations.Nullable;

public class ToolActionRecipeBuilder implements RecipeBuilder {
    private final Ingredient tool;
    private final Ingredient ingredient;
    private final int damageToTool;
    private final ItemStackTemplate result;
    private final SoundPlayback sound;

    public ToolActionRecipeBuilder(Ingredient tool, Ingredient ingredient, int damageToTool, ItemStackTemplate result, SoundPlayback sound) {
        this.tool = tool;
        this.ingredient = ingredient;
        this.damageToTool = damageToTool;
        this.result = result;
        this.sound = sound;
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(result);
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pId) {
        var recipe = new ToolActionRecipe(tool, ingredient, damageToTool, result, sound);
        pRecipeOutput.accept(pId, recipe, null);
    }
}

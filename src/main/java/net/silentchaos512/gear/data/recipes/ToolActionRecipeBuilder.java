package net.silentchaos512.gear.data.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.crafting.recipe.ToolActionRecipe;
import org.jetbrains.annotations.Nullable;

public class ToolActionRecipeBuilder implements RecipeBuilder {
    private final Ingredient tool;
    private final Ingredient ingredient;
    private final int damageToTool;
    private final ItemStack result;
    private final SoundPlayback sound;

    @Deprecated(forRemoval = true)
    public ToolActionRecipeBuilder(Ingredient tool, Ingredient ingredient, int damageToTool, ItemStack result) {
        this(tool, ingredient, damageToTool, result, new SoundPlayback(SoundEvents.STONE_HIT, 1f, 1f, 0f));
    }

    public ToolActionRecipeBuilder(Ingredient tool, Ingredient ingredient, int damageToTool, ItemStack result, SoundPlayback sound) {
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
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        var recipe = new ToolActionRecipe(tool, ingredient, damageToTool, result, sound);
        pRecipeOutput.accept(pId, recipe, null);
    }
}

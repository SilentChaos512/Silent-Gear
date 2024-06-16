package net.silentchaos512.gear.data.recipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.FabricAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.GemAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.MetalAlloyRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompoundingRecipeBuilder<R extends AlloyRecipe> implements RecipeBuilder {
    private final AlloyRecipe.Factory<R> factory;
    private final String recipeFolder;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item resultItem;
    private final int resultCount;
    @Nullable private DataResource<IMaterial> resultMaterial;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public CompoundingRecipeBuilder(AlloyRecipe.Factory<R> factory, String recipeFolder, ItemLike resultItem, int count) {
        this.factory = factory;
        this.recipeFolder = recipeFolder;
        this.resultItem = resultItem.asItem();
        this.resultCount = count;
    }

    public static CompoundingRecipeBuilder<MetalAlloyRecipe> metalBuilder(ItemLike result, int count) {
        return new CompoundingRecipeBuilder<>(MetalAlloyRecipe::new, "metal", result, count);
    }

    public static CompoundingRecipeBuilder<GemAlloyRecipe> gemBuilder(ItemLike result, int count) {
        return new CompoundingRecipeBuilder<>(GemAlloyRecipe::new, "gem", result, count);
    }

    public static CompoundingRecipeBuilder<FabricAlloyRecipe> fabricBuilder(ItemLike result, int count) {
        return new CompoundingRecipeBuilder<>(FabricAlloyRecipe::new, "fabric", result, count);
    }

    public CompoundingRecipeBuilder<R> withCustomMaterial(DataResource<IMaterial> material) {
        this.resultMaterial = material;
        return this;
    }

    public CompoundingRecipeBuilder<R> addIngredient(ItemLike item) {
        return addIngredient(Ingredient.of(item));
    }

    public CompoundingRecipeBuilder<R> addIngredient(ItemLike item, int count) {
        return addIngredient(Ingredient.of(item), count);
    }

    public CompoundingRecipeBuilder<R> addIngredient(TagKey<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public CompoundingRecipeBuilder<R> addIngredient(TagKey<Item> tag, int count) {
        return addIngredient(Ingredient.of(tag), count);
    }

    public CompoundingRecipeBuilder<R> addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient, 1);
    }

    public CompoundingRecipeBuilder<R> addIngredient(Ingredient ingredient, int count) {
        for (int i = 0; i < count; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        this.criteria.put(pName, pCriterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return resultItem;
    }

    public void save(RecipeOutput pRecipeOutput) {
        String name = "alloying/" + recipeFolder + "/" + BuiltInRegistries.ITEM.getKey(resultItem).getPath();
        if (resultMaterial != null) {
            name = name + "." + resultMaterial.getId().getPath();
        }
        save(pRecipeOutput, SilentGear.getId(name));
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        Advancement.Builder advancement$builder = null;
        if (!this.criteria.isEmpty()) {
            advancement$builder = pRecipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                    .rewards(AdvancementRewards.Builder.recipe(pId))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancement$builder::addCriterion);
        }

        var recipe = factory.create(
                new AlloyRecipe.Result(resultItem, resultCount, resultMaterial),
                ingredients
        );
        var advancementHolder = advancement$builder != null
                ? advancement$builder.build(pId.withPrefix("recipes/alloying/" + recipeFolder + "/"))
                : null;
        pRecipeOutput.accept(pId, recipe, advancementHolder);
    }
}

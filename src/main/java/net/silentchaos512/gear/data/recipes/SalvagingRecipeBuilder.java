package net.silentchaos512.gear.data.recipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.crafting.recipe.salvage.GearSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class SalvagingRecipeBuilder<R extends SalvagingRecipe> implements RecipeBuilder {
    private final BiFunction<Ingredient, List<ItemStack>, R> factory;
    private final String recipeFolder;
    private final Ingredient ingredient;
    private final List<ItemStack> results = new ArrayList<>();
    private final boolean resultsMustBePresent;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private SalvagingRecipeBuilder(BiFunction<Ingredient, List<ItemStack>, R> factory, String recipeFolder, Ingredient ingredient, boolean resultsMustBePresent) {
        this.factory = factory;
        this.recipeFolder = recipeFolder;
        this.ingredient = ingredient;
        this.resultsMustBePresent = resultsMustBePresent;
    }

    public static SalvagingRecipeBuilder<SalvagingRecipe> builder(ItemLike ingredient) {
        return builder(Ingredient.of(ingredient));
    }

    public static SalvagingRecipeBuilder<SalvagingRecipe> builder(TagKey<Item> ingredient) {
        return builder(Ingredient.of(ingredient));
    }

    public static SalvagingRecipeBuilder<SalvagingRecipe> builder(Ingredient ingredient) {
        return new SalvagingRecipeBuilder<>(SalvagingRecipe::new, "salvaging", ingredient, true);
    }

    public static SalvagingRecipeBuilder<GearSalvagingRecipe> gearBuilder(ICoreItem item) {
        return new SalvagingRecipeBuilder<>((ingredient, __) -> new GearSalvagingRecipe(ingredient), "salvaging/gear", Ingredient.of(item), false);
    }

    public SalvagingRecipeBuilder<R> addResult(ItemLike item) {
        return addResult(item, 1);
    }

    public SalvagingRecipeBuilder<R> addResult(ItemLike item, int count) {
        this.results.add(new ItemStack(item, count));
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
        return !results.isEmpty() ? results.iterator().next().getItem() : Items.AIR;
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        this.ensureValid(pId);

        Advancement.Builder advancement$builder = null;
        if (!this.criteria.isEmpty()) {
            advancement$builder = pRecipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                    .rewards(AdvancementRewards.Builder.recipe(pId))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancement$builder::addCriterion);
        }

        var recipe = factory.apply(ingredient, results);
        var advancementHolder = advancement$builder != null
                ? advancement$builder.build(pId.withPrefix("recipes/" + recipeFolder + "/"))
                : null;
        pRecipeOutput.accept(pId, recipe, advancementHolder);
    }

    private void ensureValid(ResourceLocation pId) {
        if (resultsMustBePresent && results.isEmpty()) {
            throw new IllegalStateException("Empty results for standard salvaging recipe");
        }
    }
}

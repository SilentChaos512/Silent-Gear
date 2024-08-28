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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.smithing.CoatingSmithingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.GearSmithingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.UpgradeSmithingRecipe;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class GearSmithingRecipeBuilder<R extends GearSmithingRecipe> implements RecipeBuilder {
    private final GearSmithingRecipe.Factory<R> factory;
    private final String recipeFolder;
    private final Item gearItem;
    private final Ingredient template;
    private final Ingredient addition;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public GearSmithingRecipeBuilder(GearSmithingRecipe.Factory<R> factory, String recipeFolder, Item gearItem, Ingredient template, Ingredient addition) {
        this.factory = factory;
        this.recipeFolder = recipeFolder;
        this.gearItem = gearItem;
        this.template = template;
        this.addition = addition;
    }

    public static GearSmithingRecipeBuilder<CoatingSmithingRecipe> coating(ItemLike gearItem) {
        return new GearSmithingRecipeBuilder<>(CoatingSmithingRecipe::new,
                "coating",
                gearItem.asItem(),
                Ingredient.of(SgItems.COATING_SMITHING_TEMPLATE),
                new Ingredient(PartMaterialIngredient.of(PartTypes.COATING.get()))
        );
    }

    public static GearSmithingRecipeBuilder<UpgradeSmithingRecipe> upgrade(ItemLike gearItem, PartType partType) {
        return new GearSmithingRecipeBuilder<>(UpgradeSmithingRecipe::new,
                "upgrade",
                gearItem.asItem(),
                Ingredient.of(Items.STICK),
                new Ingredient(GearPartIngredient.of(partType))
        );
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
        return this.gearItem;
    }

    public void save(RecipeOutput pRecipeOutput) {
        String name = "smithing/" + recipeFolder + "/" + BuiltInRegistries.ITEM.getKey(gearItem).getPath();
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

        var recipe = factory.create(new ItemStack(gearItem), template, addition);
        var advancementHolder = advancement$builder != null
                ? advancement$builder.build(pId.withPrefix("recipes/smithing/" + recipeFolder + "/"))
                : null;
        pRecipeOutput.accept(pId, recipe, advancementHolder);
    }
}

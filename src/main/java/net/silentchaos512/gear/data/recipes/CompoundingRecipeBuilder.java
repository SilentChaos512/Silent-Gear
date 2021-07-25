package net.silentchaos512.gear.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CompoundingRecipeBuilder {
    private final RecipeSerializer<?> serializer;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item resultItem;
    private final int resultCount;
    private DataResource<IMaterial> resultMaterial;

    public CompoundingRecipeBuilder(RecipeSerializer<?> serializer, Item resultItem, int count) {
        this.serializer = serializer;
        this.resultItem = resultItem;
        this.resultCount = count;
    }

    public static CompoundingRecipeBuilder metalBuilder(ItemLike result, int count) {
        return new CompoundingRecipeBuilder(ModRecipes.COMPOUNDING_METAL.get(), result.asItem(), count);
    }

    public static CompoundingRecipeBuilder gemBuilder(ItemLike result, int count) {
        return new CompoundingRecipeBuilder(ModRecipes.COMPOUNDING_GEM.get(), result.asItem(), count);
    }

    public CompoundingRecipeBuilder withCustomMaterial(DataResource<IMaterial> material) {
        this.resultMaterial = material;
        return this;
    }

    public CompoundingRecipeBuilder addIngredient(ItemLike item) {
        return addIngredient(Ingredient.of(item));
    }

    public CompoundingRecipeBuilder addIngredient(ItemLike item, int count) {
        return addIngredient(Ingredient.of(item), count);
    }

    public CompoundingRecipeBuilder addIngredient(Tag<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public CompoundingRecipeBuilder addIngredient(Tag<Item> tag, int count) {
        return addIngredient(Ingredient.of(tag), count);
    }

    public CompoundingRecipeBuilder addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient, 1);
    }

    public CompoundingRecipeBuilder addIngredient(Ingredient ingredient, int count) {
        for (int i = 0; i < count; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        String name = NameUtils.from(this.resultItem).getPath();
        if (resultMaterial != null) {
            name = name + "." + resultMaterial.getId().getPath();
        }
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + name));
    }

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new Result(recipeId));
    }

    public class Result implements FinishedRecipe {
        private final ResourceLocation recipeId;

        public Result(ResourceLocation recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredients", serializeIngredients());
            json.add("result", serializeResult());
        }

        private JsonArray serializeIngredients() {
            JsonArray ret = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                ret.add(ingredient.toJson());
            }
            return ret;
        }

        private JsonObject serializeResult() {
            JsonObject ret = new JsonObject();
            ret.addProperty("item", NameUtils.from(resultItem).toString());
            if (resultCount > 1) {
                ret.addProperty("count", resultCount);
            }
            if (resultMaterial != null) {
                ret.addProperty("material", resultMaterial.getId().toString());
            }
            return ret;
        }

        @Override
        public ResourceLocation getId() {
            return recipeId;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return serializer;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}

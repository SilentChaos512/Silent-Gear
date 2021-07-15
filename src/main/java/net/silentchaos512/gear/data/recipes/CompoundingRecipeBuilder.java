package net.silentchaos512.gear.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CompoundingRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item resultItem;
    private final int resultCount;
    private DataResource<IMaterial> resultMaterial;

    public CompoundingRecipeBuilder(IRecipeSerializer<?> serializer, Item resultItem, int count) {
        this.serializer = serializer;
        this.resultItem = resultItem;
        this.resultCount = count;
    }

    public static CompoundingRecipeBuilder metalBuilder(IItemProvider result, int count) {
        return new CompoundingRecipeBuilder(ModRecipes.COMPOUNDING_METAL.get(), result.asItem(), count);
    }

    public static CompoundingRecipeBuilder gemBuilder(IItemProvider result, int count) {
        return new CompoundingRecipeBuilder(ModRecipes.COMPOUNDING_GEM.get(), result.asItem(), count);
    }

    public CompoundingRecipeBuilder withCustomMaterial(DataResource<IMaterial> material) {
        this.resultMaterial = material;
        return this;
    }

    public CompoundingRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(Ingredient.of(item));
    }

    public CompoundingRecipeBuilder addIngredient(IItemProvider item, int count) {
        return addIngredient(Ingredient.of(item), count);
    }

    public CompoundingRecipeBuilder addIngredient(ITag<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public CompoundingRecipeBuilder addIngredient(ITag<Item> tag, int count) {
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

    public void build(Consumer<IFinishedRecipe> consumer) {
        String name = NameUtils.from(this.resultItem).getPath();
        if (resultMaterial != null) {
            name = name + "." + resultMaterial.getId().getPath();
        }
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + name));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new Result(recipeId));
    }

    public class Result implements IFinishedRecipe {
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
        public IRecipeSerializer<?> getType() {
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

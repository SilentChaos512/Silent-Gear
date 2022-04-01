package net.silentchaos512.gear.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public final class SalvagingRecipeBuilder {
    private final Ingredient ingredient;
    private final RecipeSerializer<?> serializer;
    private final Collection<ItemStack> results = new ArrayList<>();

    private SalvagingRecipeBuilder(Ingredient ingredient, RecipeSerializer<?> serializer) {
        this.ingredient = ingredient;
        this.serializer = serializer;
    }

    public static SalvagingRecipeBuilder builder(ItemLike ingredient) {
        return builder(Ingredient.of(ingredient));
    }

    public static SalvagingRecipeBuilder builder(TagKey<Item> ingredient) {
        return builder(Ingredient.of(ingredient));
    }

    public static SalvagingRecipeBuilder builder(Ingredient ingredient) {
        return new SalvagingRecipeBuilder(ingredient, ModRecipes.SALVAGING.get());
    }

    public static SalvagingRecipeBuilder gearBuilder(ICoreItem item) {
        return new SalvagingRecipeBuilder(Ingredient.of(item), ModRecipes.SALVAGING_GEAR.get());
    }

    public SalvagingRecipeBuilder addResult(ItemLike item) {
        return addResult(item, 1);
    }

    public SalvagingRecipeBuilder addResult(ItemLike item, int count) {
        this.results.add(new ItemStack(item, count));
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (this.serializer == ModRecipes.SALVAGING.get() && this.results.isEmpty()) {
            throw new IllegalStateException("Empty results for standard salvaging recipe");
        }
        consumer.accept(new Result(id, this));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final SalvagingRecipeBuilder builder;

        public Result(ResourceLocation id, SalvagingRecipeBuilder builder) {
            this.id = id;
            this.builder = builder;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", builder.ingredient.toJson());

            if (!builder.results.isEmpty()) {
                JsonArray results = new JsonArray();
                builder.results.forEach(stack -> results.add(serializeItem(stack)));
                json.add("results", results);
            }
        }

        private JsonObject serializeItem(ItemStack stack) {
            JsonObject json = new JsonObject();
            json.addProperty("item", NameUtils.fromItem(stack).toString());
            if (stack.getCount() > 1) {
                json.addProperty("count", stack.getCount());
            }
            return json;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return builder.serializer;
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

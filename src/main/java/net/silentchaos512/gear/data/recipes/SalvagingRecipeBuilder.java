package net.silentchaos512.gear.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public final class SalvagingRecipeBuilder {
    private final Ingredient ingredient;
    private final IRecipeSerializer<?> serializer;
    private final Collection<ItemStack> results = new ArrayList<>();

    private SalvagingRecipeBuilder(Ingredient ingredient, IRecipeSerializer<?> serializer) {
        this.ingredient = ingredient;
        this.serializer = serializer;
    }

    public static SalvagingRecipeBuilder builder(IItemProvider ingredient) {
        return builder(Ingredient.fromItems(ingredient));
    }

    public static SalvagingRecipeBuilder builder(Tag<Item> ingredient) {
        return builder(Ingredient.fromTag(ingredient));
    }

    public static SalvagingRecipeBuilder builder(Ingredient ingredient) {
        return new SalvagingRecipeBuilder(ingredient, ModRecipes.SALVAGING.get());
    }

    public static SalvagingRecipeBuilder gearBuilder(ICoreItem item) {
        return new SalvagingRecipeBuilder(Ingredient.fromItems(item), ModRecipes.SALVAGING_GEAR.get());
    }

    public SalvagingRecipeBuilder addResult(IItemProvider item) {
        return addResult(item, 1);
    }

    public SalvagingRecipeBuilder addResult(IItemProvider item, int count) {
        this.results.add(new ItemStack(item, count));
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        if (this.serializer == ModRecipes.SALVAGING.get() && this.results.isEmpty()) {
            throw new IllegalStateException("Empty results for standard salvaging recipe");
        }
        consumer.accept(new Result(id, this));
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final SalvagingRecipeBuilder builder;

        public Result(ResourceLocation id, SalvagingRecipeBuilder builder) {
            this.id = id;
            this.builder = builder;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("ingredient", builder.ingredient.serialize());

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
        public ResourceLocation getID() {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return builder.serializer;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return null;
        }
    }
}

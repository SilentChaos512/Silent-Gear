package net.silentchaos512.gear.crafting.recipe.compounder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.compounder.CompounderInfo;
import net.silentchaos512.gear.block.compounder.CompounderTileEntity;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.item.CustomMaterialItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CompoundingRecipe implements Recipe<CompounderTileEntity<?>> {
    private final ResourceLocation recipeId;
    final List<Ingredient> ingredients = new ArrayList<>();
    ItemStack result = ItemStack.EMPTY;

    public CompoundingRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public static CompoundingRecipe makeExample(CompounderInfo<?> info, int count, CompoundingRecipe recipe) {
        IMaterialCategory[] cats = info.getCategories().toArray(new IMaterialCategory[0]);
        for (int i = 0; i < count; ++i) {
            recipe.ingredients.add(PartMaterialIngredient.of(PartType.MAIN, GearType.ALL, cats));
        }
        recipe.result = new ItemStack(info.getOutputItem(), count);
        return recipe;
    }

    @Override
    public boolean matches(CompounderTileEntity<?> inv, Level worldIn) {
        Set<Integer> matches = new HashSet<>();
        int inputs = 0;

        for (int i = 0; i < inv.getInputSlotCount(); ++i) {
            if (!inv.getItem(i).isEmpty()) {
                ++inputs;
            }
        }

        for (Ingredient ingredient : this.ingredients) {
            boolean found = false;

            for (int i = 0; i < inv.getInputSlotCount(); ++i) {
                ItemStack stack = inv.getItem(i);

                if (!stack.isEmpty() && ingredient.test(stack)) {
                    found = true;
                    matches.add(i);
                }
            }

            if (!found) {
                return false;
            }
        }

        int matchCount = matches.size();
        return matchCount == inputs && matchCount == this.ingredients.size();
    }

    @Override
    public ItemStack assemble(CompounderTileEntity<?> inv, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height <= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ret = NonNullList.create();
        ret.addAll(ingredients);
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.COMPOUNDING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SgRecipes.COMPOUNDING_TYPE.get();
    }

    public static class Serializer<T extends CompoundingRecipe> implements RecipeSerializer<T> {
        private final Function<ResourceLocation, T> factory;

        public Serializer(Function<ResourceLocation, T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            T ret = this.factory.apply(recipeId);

            JsonArray array = json.getAsJsonArray("ingredients");
            for (JsonElement je : array) {
                ret.ingredients.add(Ingredient.fromJson(je));
            }

            JsonObject resultJson = json.getAsJsonObject("result");
            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(resultJson, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                throw new JsonParseException("Unknown item: " + itemId);
            }
            int count = GsonHelper.getAsInt(resultJson, "count", 1);

            if (item instanceof CustomMaterialItem && resultJson.has("material")) {
                ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(resultJson, "material"));
                ret.result = ((CustomMaterialItem) item).create(LazyMaterialInstance.of(id), count);
            }

            if (ret.result.isEmpty()) {
                ret.result = new ItemStack(item, count);
            }

            return ret;
        }

        @Nullable
        @Override
        public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            T ret = this.factory.apply(recipeId);

            int count = buffer.readByte();
            for (int i = 0; i < count; ++i) {
                ret.ingredients.add(Ingredient.fromNetwork(buffer));
            }

            ret.result = buffer.readItem();

            return ret;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, T recipe) {
            buffer.writeByte(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient -> ingredient.toNetwork(buffer));
            buffer.writeItem(recipe.result);
        }
    }
}

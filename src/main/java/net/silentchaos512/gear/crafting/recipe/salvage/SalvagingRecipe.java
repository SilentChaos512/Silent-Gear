package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.init.ModRecipes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SalvagingRecipe implements IRecipe<IInventory> {
    private final ResourceLocation recipeId;
    protected Ingredient ingredient;
    private final List<ItemStack> results = new ArrayList<>();

    public SalvagingRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getPossibleResults(IInventory inv) {
        return new ArrayList<>(results);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return ingredient.test(inv.getStackInSlot(0));
    }

    @Deprecated
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        // DO NOT USE
        return getRecipeOutput();
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getRecipeOutput() {
        // DO NOT USE
        return !results.isEmpty() ? results.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SALVAGING_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.SALVAGING_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SalvagingRecipe> {
        @Override
        public SalvagingRecipe read(ResourceLocation recipeId, JsonObject json) {
            SalvagingRecipe recipe = new SalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            JsonArray resultsArray = json.getAsJsonArray("results");
            for (JsonElement element : resultsArray) {
                if (element.isJsonObject()) {
                    Item item = JSONUtils.getItem(element.getAsJsonObject(), "item");
                    int count = JSONUtils.getInt(element.getAsJsonObject(), "count", 1);
                    recipe.results.add(new ItemStack(item, count));
                } else {
                    recipe.results.add(new ItemStack(JSONUtils.getItem(element, "item")));
                }
            }
            return recipe;
        }

        @Nullable
        @Override
        public SalvagingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            SalvagingRecipe recipe = new SalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.read(buffer);
            int resultCount = buffer.readByte();
            for (int i = 0; i < resultCount; ++i) {
                recipe.results.add(buffer.readItemStack());
            }
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, SalvagingRecipe recipe) {
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.results.size());
            recipe.results.forEach(buffer::writeItemStack);
        }
    }
}

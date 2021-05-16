package net.silentchaos512.gear.crafting.recipe.compounder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.compounder.CompounderInfo;
import net.silentchaos512.gear.block.compounder.CompounderTileEntity;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.item.CustomMaterialItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CompoundingRecipe implements IRecipe<CompounderTileEntity<?>> {
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
    public boolean matches(CompounderTileEntity<?> inv, World worldIn) {
        Set<Integer> matches = new HashSet<>();
        int inputs = 0;

        for (int i = 0; i < inv.getInputSlotCount(); ++i) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                ++inputs;
            }
        }

        for (Ingredient ingredient : this.ingredients) {
            boolean found = false;

            for (int i = 0; i < inv.getInputSlotCount(); ++i) {
                ItemStack stack = inv.getStackInSlot(i);

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
    public ItemStack getCraftingResult(CompounderTileEntity<?> inv) {
        return this.result.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height <= this.ingredients.size();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ret = NonNullList.create();
        ret.addAll(ingredients);
        return ret;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.COMPOUNDING.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.COMPOUNDING_TYPE;
    }

    public static class Serializer<T extends CompoundingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        private final Function<ResourceLocation, T> factory;

        public Serializer(Function<ResourceLocation, T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation recipeId, JsonObject json) {
            T ret = this.factory.apply(recipeId);

            JsonArray array = json.getAsJsonArray("ingredients");
            for (JsonElement je : array) {
                ret.ingredients.add(Ingredient.deserialize(je));
            }

            JsonObject resultJson = json.getAsJsonObject("result");
            ResourceLocation itemId = new ResourceLocation(JSONUtils.getString(resultJson, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                throw new JsonParseException("Unknown item: " + itemId);
            }
            int count = JSONUtils.getInt(resultJson, "count", 1);

            if (item instanceof CustomMaterialItem && resultJson.has("material")) {
                ResourceLocation id = new ResourceLocation(JSONUtils.getString(resultJson, "material"));
                ret.result = ((CustomMaterialItem) item).create(LazyMaterialInstance.of(id), count);
            }

            if (ret.result.isEmpty()) {
                ret.result = new ItemStack(item, count);
            }

            return ret;
        }

        @Nullable
        @Override
        public T read(ResourceLocation recipeId, PacketBuffer buffer) {
            T ret = this.factory.apply(recipeId);

            int count = buffer.readByte();
            for (int i = 0; i < count; ++i) {
                ret.ingredients.add(Ingredient.read(buffer));
            }

            ret.result = buffer.readItemStack();

            return ret;
        }

        @Override
        public void write(PacketBuffer buffer, T recipe) {
            buffer.writeByte(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient -> ingredient.write(buffer));
            buffer.writeItemStack(recipe.result);
        }
    }
}

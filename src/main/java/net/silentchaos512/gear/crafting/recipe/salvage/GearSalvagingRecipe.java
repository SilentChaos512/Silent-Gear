package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonObject;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.init.SgRecipes;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GearSalvagingRecipe extends SalvagingRecipe {
    public GearSalvagingRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public List<ItemStack> getPossibleResults(Container inv) {
        ItemStack input = inv.getItem(0);
        List<ItemStack> ret = new ArrayList<>();

        PartDataList parts = GearData.getConstructionParts(input);
        for (PartData part : parts) {
            ret.addAll(salvage(part));
        }

        return ret;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING_GEAR.get();
    }

    public static class Serializer implements RecipeSerializer<GearSalvagingRecipe> {
        @Override
        public GearSalvagingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            GearSalvagingRecipe recipe = new GearSalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            return recipe;
        }

        @Nullable
        @Override
        public GearSalvagingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            GearSalvagingRecipe recipe = new GearSalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GearSalvagingRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
        }
    }
}

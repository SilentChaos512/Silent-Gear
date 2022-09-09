package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonObject;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.gear.part.PartData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompoundPartSalvagingRecipe extends SalvagingRecipe {
    public CompoundPartSalvagingRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public List<ItemStack> getPossibleResults(Container inv) {
        ItemStack input = inv.getItem(0);
        List<ItemStack> ret = new ArrayList<>();

        PartData part = PartData.from(input);
        if (part != null) {
            ret.addAll(salvage(part));
        }

        return ret;
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredient.of(Registration.getItems(CompoundPartItem.class).toArray(new CompoundPartItem[0]));
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (!(inv.getItem(0).getItem() instanceof CompoundPartItem))
            return false;

        return PartData.from(inv.getItem(0)) != null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SALVAGING_COMPOUND_PART.get();
    }

    public static class Serializer implements RecipeSerializer<CompoundPartSalvagingRecipe> {
        @Override
        public CompoundPartSalvagingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new CompoundPartSalvagingRecipe(recipeId);
        }

        @Nullable
        @Override
        public CompoundPartSalvagingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new CompoundPartSalvagingRecipe(recipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompoundPartSalvagingRecipe recipe) {
        }
    }
}

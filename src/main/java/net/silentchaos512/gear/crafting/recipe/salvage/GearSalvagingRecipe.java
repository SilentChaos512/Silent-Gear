package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.init.ModRecipes;
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
    public List<ItemStack> getPossibleResults(IInventory inv) {
        ItemStack input = inv.getStackInSlot(0);
        List<ItemStack> ret = new ArrayList<>();

        PartDataList parts = GearData.getConstructionParts(input);
        for (PartData part : parts) {
            ret.addAll(salvage(part));
        }

        return ret;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SALVAGING_GEAR.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GearSalvagingRecipe> {
        @Override
        public GearSalvagingRecipe read(ResourceLocation recipeId, JsonObject json) {
            GearSalvagingRecipe recipe = new GearSalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            return recipe;
        }

        @Nullable
        @Override
        public GearSalvagingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            GearSalvagingRecipe recipe = new GearSalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.read(buffer);
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, GearSalvagingRecipe recipe) {
            recipe.ingredient.write(buffer);
        }
    }
}

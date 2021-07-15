package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
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
    public List<ItemStack> getPossibleResults(IInventory inv) {
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
    public boolean matches(IInventory inv, World worldIn) {
        if (!(inv.getItem(0).getItem() instanceof CompoundPartItem))
            return false;

        return PartData.from(inv.getItem(0)) != null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SALVAGING_COMPOUND_PART.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CompoundPartSalvagingRecipe> {
        @Override
        public CompoundPartSalvagingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new CompoundPartSalvagingRecipe(recipeId);
        }

        @Nullable
        @Override
        public CompoundPartSalvagingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            return new CompoundPartSalvagingRecipe(recipeId);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, CompoundPartSalvagingRecipe recipe) {
        }
    }
}

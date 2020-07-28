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
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.parts.PartData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompoundPartSalvagingRecipe extends SalvagingRecipe {
    public CompoundPartSalvagingRecipe(ResourceLocation recipeId) {
        super(recipeId);
    }

    @Override
    public List<ItemStack> getPossibleResults(IInventory inv) {
        ItemStack input = inv.getStackInSlot(0);
        List<ItemStack> ret = new ArrayList<>();

        if (input.getItem() instanceof CompoundPartItem) {
            CompoundPartItem.getMaterials(input).stream()
                    .map(MaterialInstance::getItem)
                    .filter(s -> !s.isEmpty())
                    .forEach(ret::add);
        }

        return ret;
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredient.fromItems(Registration.getItems(CompoundPartItem.class).toArray(new CompoundPartItem[0]));
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (!(inv.getStackInSlot(0).getItem() instanceof CompoundPartItem))
            return false;

        // FIXME: How to allow salvaging for other parts? Issue #191
        PartData part = PartData.from(inv.getStackInSlot(0));
        return part != null && part.getType() == PartType.MAIN;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SALVAGING_COMPOUND_PART_SERIALIZER;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CompoundPartSalvagingRecipe> {
        @Override
        public CompoundPartSalvagingRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new CompoundPartSalvagingRecipe(recipeId);
        }

        @Nullable
        @Override
        public CompoundPartSalvagingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new CompoundPartSalvagingRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, CompoundPartSalvagingRecipe recipe) {
        }
    }
}

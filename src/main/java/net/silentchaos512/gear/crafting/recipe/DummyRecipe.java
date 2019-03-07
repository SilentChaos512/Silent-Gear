package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;

public class DummyRecipe implements IRecipe {
    private final ResourceLocation recipeId;

    public DummyRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return new Serializer();
    }

    private static class Serializer implements IRecipeSerializer<DummyRecipe> {
        @Override
        public DummyRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new DummyRecipe(recipeId);
        }

        @Override
        public DummyRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new DummyRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, DummyRecipe recipe) { }

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(SilentGear.MOD_ID, "dummy_recipe");
        }
    }
}

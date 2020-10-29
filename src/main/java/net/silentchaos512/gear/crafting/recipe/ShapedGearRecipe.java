package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;

public final class ShapedGearRecipe extends ExtendedShapedRecipe implements IGearRecipe {
    private final ICoreItem item;

    public ShapedGearRecipe(ShapedRecipe recipe) {
        super(recipe);

        ItemStack output = recipe.getRecipeOutput();
        if (!(output.getItem() instanceof ICoreItem)) {
            throw new JsonParseException("result is not a gear item: " + output);
        }
        this.item = (ICoreItem) output.getItem();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPED_GEAR.get();
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (!this.getBaseRecipe().matches(inv, worldIn)) return false;

        GearType gearType = item.getGearType();
        return getParts(inv).stream().allMatch(part -> part.isCraftingAllowed(gearType, inv));
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return item.construct(getParts(inv));
    }

    @Override
    public ICoreItem getOutputItem() {
        return item;
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Create an example item, so we're not just showing a broken item
        ItemStack result = item.construct(GearHelper.getExamplePartsFromRecipe(this.item.getGearType(), getIngredients()));
        GearData.setExampleTag(result, true);
        return result;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}

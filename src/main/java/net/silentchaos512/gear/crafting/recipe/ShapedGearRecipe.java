package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;

public final class ShapedGearRecipe extends ExtendedShapedRecipe implements IGearRecipe {
    private final GearItem item;
    private final Lazy<ItemStack> exampleOutput;

    public ShapedGearRecipe(String pGroup, CraftingBookCategory pCategory, ShapedRecipePattern pPattern, ItemStack pResult, boolean pShowNotification) {
        super(pGroup, pCategory, pPattern, pResult, pShowNotification);

        if (!(pResult.getItem() instanceof GearItem)) {
            throw new JsonParseException("result is not a gear item: " + pResult);
        }
        this.item = (GearItem) pResult.getItem();

        this.exampleOutput = Lazy.of(() -> {
            // Create an example item, so we're not just showing a broken item
            ItemStack result = item.construct(GearHelper.getExamplePartsFromRecipe(this.item.getGearType(), getIngredients()));
            GearData.setExampleTag(result, true);
            return result;
        });
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SHAPED_GEAR.get();
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        if (!super.matches(inv, worldIn)) return false;

        GearType gearType = item.getGearType();
        for (PartInstance part : getParts(inv)) {
            if (!part.isCraftingAllowed(gearType, inv)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        return item.construct(getParts(inv));
    }

    @Override
    public GearItem getOutputItem() {
        return item;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return exampleOutput.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}

package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.Collection;

public final class ShapelessGearRecipe extends ExtendedShapelessRecipe implements IGearRecipe {
    private final ICoreItem item;
    private final Lazy<ItemStack> exampleOutput;

    public ShapelessGearRecipe(ShapelessRecipe recipe) {
        super(recipe);

        ItemStack output = recipe.getResultItem();
        if (!(output.getItem() instanceof ICoreItem)) {
            throw new JsonParseException("result is not a gear item: " + output);
        }
        this.item = (ICoreItem) output.getItem();

        this.exampleOutput = Lazy.of(() -> {
            // Create an example item, so we're not just showing a broken item
            ItemStack result = item.construct(GearHelper.getExamplePartsFromRecipe(this.item.getGearType(), getIngredients()));
            GearData.setExampleTag(result, true);
            return result;
        });
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_GEAR.get();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        if (!this.getBaseRecipe().matches(inv, worldIn)) return false;

        GearType gearType = item.getGearType();
        Collection<PartData> parts = getParts(inv);

        if (parts.isEmpty()) return false;

        for (PartData part : parts) {
            if (!part.isCraftingAllowed(gearType, inv)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        return item.construct(getParts(inv));
    }

    @Override
    public ICoreItem getOutputItem() {
        return item;
    }

    @Override
    public ItemStack getResultItem() {
        return exampleOutput.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}

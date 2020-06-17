package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.util.Size2i;
import net.silentchaos512.gear.init.ModItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GearCraftingRecipeCategoryJei implements IRecipeCategory<ICraftingRecipe> {
    public static final int width = 116;
    public static final int height = 54;
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public GearCraftingRecipeCategoryJei(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
        this.background = guiHelper.createDrawable(location, 0, 60, 116, 54);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.blueprintPackage));
        this.localizedName = I18n.format("gui.silentgear.category.gearCrafting");
        this.craftingGridHelper = guiHelper.createCraftingGridHelper(1);
    }

    @Override
    public ResourceLocation getUid() {
        return SGearJeiPlugin.GEAR_CRAFTING;
    }

    @Override
    public Class<? extends ICraftingRecipe> getRecipeClass() {
        return ICraftingRecipe.class;
    }

    @Override
    public String getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ICraftingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, false, 94, 18);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = 1 + x + y * 3;
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        List<List<ItemStack>> shiftedInputs = new ArrayList<>();
        for (int i = 0; i < inputs.size(); ++i) {
            shiftedInputs.add(shiftIngredients(inputs.get(i), 5 * i));
        }

        Size2i size = getRecipeSize(recipe);
        if (size != null && size.width > 0 && size.height > 0) {
            this.craftingGridHelper.setInputs(guiItemStacks, shiftedInputs, size.width, size.height);
        } else {
            this.craftingGridHelper.setInputs(guiItemStacks, shiftedInputs);
            recipeLayout.setShapeless();
        }

        guiItemStacks.set(0, outputs.get(0));
    }

    private static List<ItemStack> shiftIngredients(List<ItemStack> list, int amount) {
        List<ItemStack> ret = new ArrayList<>(list);
        for (int i = 0; i < amount; ++i) {
            ItemStack stack = ret.get(ret.size() - 1);
            ret.remove(ret.size() - 1);
            ret.add(0, stack);
        }
        return ret;
    }

    @Override
    public void setIngredients(ICraftingRecipe recipe, IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        ingredients.setInputIngredients(recipe.getIngredients());
    }

    @Nullable
    private static Size2i getRecipeSize(ICraftingRecipe recipe) {
        if (recipe instanceof IShapedRecipe) {
            IShapedRecipe shapedRecipe = (IShapedRecipe) recipe;
            return new Size2i(shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight());
        } else {
            return null;
        }
    }
}

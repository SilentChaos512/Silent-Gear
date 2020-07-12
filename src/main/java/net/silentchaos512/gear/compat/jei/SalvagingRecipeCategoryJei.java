package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SalvagingRecipeCategoryJei implements IRecipeCategory<SalvagingRecipe> {
    private static final int GUI_START_X = 8;
    private static final int GUI_START_Y = 16;
    private static final int GUI_WIDTH = 114 - GUI_START_X;
    private static final int GUI_HEIGHT = 69 - GUI_START_Y;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final String localizedName;

    public SalvagingRecipeCategoryJei(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(SalvagerScreen.TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SALVAGER));
        arrow = guiHelper.drawableBuilder(SalvagerScreen.TEXTURE, 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        localizedName = TextUtil.translate("jei", "category.salvaging").getFormattedText();
    }

    @Override
    public ResourceLocation getUid() {
        return Const.SALVAGING;
    }

    @Override
    public Class<? extends SalvagingRecipe> getRecipeClass() {
        return SalvagingRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(SalvagingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(Collections.singletonList(recipe.getIngredient()));
        ingredients.setOutputs(VanillaTypes.ITEM, new ArrayList<>(recipe.getPossibleResults(new Inventory(SalvagerTileEntity.INVENTORY_SIZE))));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SalvagingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 8 - GUI_START_X, 34 - GUI_START_Y);
        for (int i = 1; i < 10; ++i) {
            int x = 18 * ((i - 1) % 3) + 61 - GUI_START_X;
            int y = 18 * ((i - 1) / 3) + 16 - GUI_START_Y;
            itemStacks.init(i, false, x, y);
        }

        itemStacks.set(0, Arrays.asList(recipe.getIngredient().getMatchingStacks()));
        List<ItemStack> results = recipe.getPossibleResults(new Inventory(1));
        for (int i = 0; i < 9 && i < results.size(); ++i) {
            itemStacks.set(i + 1, results.get(i));
        }
    }

    @Override
    public void draw(SalvagingRecipe recipe, double mouseX, double mouseY) {
        arrow.draw(32 - GUI_START_X, 34 - GUI_START_Y);
    }
}

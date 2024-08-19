package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SalvagingRecipeCategoryJei implements IRecipeCategory<SalvagingRecipe> {
    private static final int GUI_START_X = 9;
    private static final int GUI_START_Y = 17;
    private static final int GUI_WIDTH = 114 - GUI_START_X;
    private static final int GUI_HEIGHT = 69 - GUI_START_Y;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final Component localizedName;

    public SalvagingRecipeCategoryJei(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(SalvagerScreen.TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SgBlocks.SALVAGER));
        arrow = guiHelper.drawableBuilder(SalvagerScreen.TEXTURE, 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        localizedName = TextUtil.translate("jei", "group.salvaging");
    }

    @Override
    public RecipeType<SalvagingRecipe> getRecipeType() {
        return SGearJeiPlugin.SALVAGING_TYPE;
    }

    @Override
    public Component getTitle() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, SalvagingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8 - GUI_START_X, 34 - GUI_START_Y)
                .addIngredients(VanillaTypes.ITEM_STACK, Arrays.asList(recipe.getIngredient().getItems()));

        List<ItemStack> results = recipe.getPossibleResults(new SimpleContainer(1));

        for (int i = 0; i < 9 && i < results.size(); ++i) {
            int x = 18 * (i % 3) + 61 - GUI_START_X;
            int y = 18 * (i / 3) + 16 - GUI_START_Y;
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addIngredients(VanillaTypes.ITEM_STACK, Collections.singletonList(results.get(i)));
        }
    }

    @Override
    public void draw(SalvagingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 32 - GUI_START_X, 34 - GUI_START_Y);
    }
}

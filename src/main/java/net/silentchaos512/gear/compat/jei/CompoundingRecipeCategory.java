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
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.block.compounder.AlloyMakerInfo;
import net.silentchaos512.gear.block.compounder.AlloyMakerScreen;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipe;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompoundingRecipeCategory implements IRecipeCategory<AlloyRecipe> {

    private static final int GUI_START_X = 15;
    private static final int GUI_START_Y = 29;
    private static final int GUI_WIDTH = 147 - GUI_START_X;
    private static final int GUI_HEIGHT = 56 - GUI_START_Y;

    private final AlloyMakerInfo<?> info;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final Component localizedName;

    public CompoundingRecipeCategory(AlloyMakerInfo<?> info, String categoryName, IGuiHelper guiHelper) {
        this.info = info;
        background = guiHelper.createDrawable(AlloyMakerScreen.TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(info.getBlock()));
        arrow = guiHelper.drawableBuilder(AlloyMakerScreen.TEXTURE, 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        localizedName = TextUtil.translate("jei", "group.compounding." + categoryName);
    }

    @Override
    public RecipeType<AlloyRecipe> getRecipeType() {
        if (this.info == Const.FABRIC_COMPOUNDER_INFO) {
            return SGearJeiPlugin.COMPOUNDING_FABRIC_TYPE;
        } else if (this.info == Const.GEM_COMPOUNDER_INFO) {
            return SGearJeiPlugin.COMPOUNDING_GEM_TYPE;
        } else if (this.info == Const.METAL_COMPOUNDER_INFO) {
            return SGearJeiPlugin.COMPOUNDING_METAL_TYPE;
        } else {
            throw new IllegalStateException("Unknown JEI recipe type: " + this.info.getRecipeType());
        }
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
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < info.getInputSlotCount() && i < recipe.getIngredients().size(); ++i) {
            List<ItemStack> items = Arrays.asList(recipe.getIngredients().get(i).getItems());
            builder.addSlot(RecipeIngredientRole.INPUT, 18 * i + 17 - GUI_START_X, 35 - GUI_START_Y)
                    .addIngredients(VanillaTypes.ITEM_STACK, shiftIngredients(items, 3 * i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 126 - GUI_START_X, 35 - GUI_START_Y)
                        .addIngredients(VanillaTypes.ITEM_STACK, Collections.singletonList(recipe.getResultItem(null)));
    }

    private static List<ItemStack> shiftIngredients(List<ItemStack> list, int amount) {
        List<ItemStack> ret = new ArrayList<>(list);
        if (ret.isEmpty()) {
            return ret;
        }
        for (int i = 0; i < amount; ++i) {
            ItemStack stack = ret.get(ret.size() - 1);
            ret.remove(ret.size() - 1);
            ret.add(0, stack);
        }
        return ret;
    }

    @Override
    public void draw(AlloyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 93 - GUI_START_X, 34 - GUI_START_Y);
    }
}

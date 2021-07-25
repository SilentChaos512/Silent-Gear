package net.silentchaos512.gear.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.block.compounder.CompounderInfo;
import net.silentchaos512.gear.block.compounder.CompounderScreen;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompoundingRecipeCategory<R extends CompoundingRecipe> implements IRecipeCategory<R> {
    private static final int GUI_START_X = 15;
    private static final int GUI_START_Y = 29;
    private static final int GUI_WIDTH = 147 - GUI_START_X;
    private static final int GUI_HEIGHT = 56 - GUI_START_Y;

    private final CompounderInfo<R> info;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final String localizedName;

    public CompoundingRecipeCategory(CompounderInfo<R> info, String categoryName, IGuiHelper guiHelper) {
        this.info = info;
        background = guiHelper.createDrawable(CompounderScreen.TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(info.getBlock()));
        arrow = guiHelper.drawableBuilder(CompounderScreen.TEXTURE, 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        localizedName = TextUtil.translate("jei", "category.compounding." + categoryName).getString();
    }

    @Override
    public ResourceLocation getUid() {
        return NameUtils.from(info.getRecipeSerializer());
    }

    @Override
    public Class<? extends R> getRecipeClass() {
        return info.getRecipeClass();
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
    public void setIngredients(R recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, R recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        for (int i = 0; i < info.getInputSlotCount(); ++i) {
            itemStacks.init(i, true, 18 * i + 16 - GUI_START_X, 34 - GUI_START_Y);
        }
        for (int i = 0; i < recipe.getIngredients().size(); ++i) {
            List<ItemStack> list = Arrays.asList(recipe.getIngredients().get(i).getItems());
            itemStacks.set(i, shiftIngredients(list, 3 * i));
        }
        itemStacks.init(info.getInputSlotCount(), false, 125 - GUI_START_X, 34 - GUI_START_Y);
        itemStacks.set(info.getInputSlotCount(), Collections.singletonList(recipe.getResultItem()));
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
    public void draw(R recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 93 - GUI_START_X, 34 - GUI_START_Y);
    }
}

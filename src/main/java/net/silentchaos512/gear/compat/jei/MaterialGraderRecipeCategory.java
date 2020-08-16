package net.silentchaos512.gear.compat.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialGraderRecipeCategory implements IRecipeCategory<MaterialGraderRecipeCategory.GraderRecipe> {
    private static final int GUI_START_X = 24;
    private static final int GUI_START_Y = 33;
    private static final int GUI_WIDTH = 43 - GUI_START_X;
    private static final int GUI_HEIGHT = 72 - GUI_START_Y;

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public MaterialGraderRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(GraderScreen.TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.MATERIAL_GRADER));
        localizedName = TextUtil.translate("jei", "category.grading").getString();
    }

    @Override
    public ResourceLocation getUid() {
        return Const.GRADING;
    }

    @Override
    public Class<? extends GraderRecipe> getRecipeClass() {
        return GraderRecipe.class;
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
    public void setIngredients(GraderRecipe graderRecipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM,
                ImmutableList.of(
                        getMaterials(),
                        getCatalysts()
                )
        );
    }

    @Nonnull
    public static List<ItemStack> getMaterials() {
        return MaterialManager.getValues().stream()
                .map(IMaterial::getIngredient)
                .flatMap(ing -> Arrays.stream(ing.getMatchingStacks()))
                .collect(Collectors.toList());
    }

    @Nonnull
    public static List<ItemStack> getCatalysts() {
        return ModTags.Items.GRADER_CATALYSTS.getAllElements().stream()
                .map(ItemStack::new)
                .collect(Collectors.toList());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GraderRecipe graderRecipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 1, 1);
        itemStacks.init(1, true, 1, 21);

        itemStacks.set(0, getMaterials());
        itemStacks.set(1, getCatalysts());
    }

    public static class GraderRecipe {
    }
}

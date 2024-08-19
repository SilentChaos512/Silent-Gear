package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgTags;
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
    private final Component localizedName;

    public MaterialGraderRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(GraderScreen.TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SgBlocks.MATERIAL_GRADER));
        localizedName = TextUtil.translate("jei", "group.grading");
    }

    @Override
    public RecipeType<GraderRecipe> getRecipeType() {
        return SGearJeiPlugin.GRADING_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, GraderRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
                .addIngredients(VanillaTypes.ITEM_STACK, getMaterials());
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 22)
                .addIngredients(VanillaTypes.ITEM_STACK, getCatalysts());
    }

    @Nonnull
    public static List<ItemStack> getMaterials() {
        return MaterialManager.getValues().stream()
                .map(Material::getIngredient)
                .flatMap(ing -> Arrays.stream(ing.getItems()))
                .collect(Collectors.toList());
    }

    @Nonnull
    public static List<ItemStack> getCatalysts() {
        return BuiltInRegistries.ITEM.stream()
                .map(ItemStack::new)
                .filter(stack -> stack.is(SgTags.Items.GRADER_CATALYSTS))
                .collect(Collectors.toList());
    }

    public static class GraderRecipe {
    }
}

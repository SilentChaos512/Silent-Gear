package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.block.analyzer.PartAnalyzerTileEntity;
import net.silentchaos512.gear.init.ModBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PartAnalyzerCategory implements IRecipeCategory<PartAnalyzerCategory.Recipe> {
    private static final int GUI_START_X = 0;
    private static final int GUI_START_Y = 0;
    private static final int GUI_WIDTH = 72;
    private static final int GUI_HEIGHT = 38;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final String localizedName;

    public PartAnalyzerCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(SGearJeiPlugin.GUI_TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.PART_ANALYZER));
        arrow = guiHelper.drawableBuilder(SGearJeiPlugin.GUI_TEXTURE, 72, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        localizedName = I18n.format("category.silentgear.part_analyzer");
    }

    @Override
    public ResourceLocation getUid() {
        return SGearJeiPlugin.PART_ANALYZER;
    }

    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
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
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        List<ItemStack> materials = recipe.getInputs();
        List<ItemStack> catalysts = Recipe.getCatalysts();
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(materials, catalysts));

        List<ItemStack> outputs = new ArrayList<>();
        for (MaterialGrade grade : MaterialGrade.values()) {
            if (grade != MaterialGrade.NONE) {
                outputs.addAll(materials.stream().map(grade::applyTo).collect(Collectors.toList()));
            }
        }
        ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(outputs));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 0, 0);
        itemStacks.init(1, true, 0, 20);
        itemStacks.init(2, false, 54, 0);

        itemStacks.set(ingredients);
    }

    @Override
    public void draw(Recipe recipe, double mouseX, double mouseY) {
        arrow.draw(24, 0);
    }

    static final class Recipe {
        final IGearPart part;

        Recipe(IGearPart part) {
            this.part = part;
        }

        boolean isValid() {
            return !this.getInputs().isEmpty();
        }

        List<ItemStack> getInputs() {
            return Arrays.asList(part.getMaterials().getNormal().getMatchingStacks());
        }

        static List<ItemStack> getCatalysts() {
            List<ItemStack> list = new ArrayList<>();
            PartAnalyzerTileEntity.CATALYST_TAGS.forEach(tag ->
                    list.addAll(tag.getAllElements().stream().map(ItemStack::new).collect(Collectors.toList())));
            return list;
        }
    }
}
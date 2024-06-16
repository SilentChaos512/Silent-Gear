package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.recipe.ToolActionRecipe;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.util.TextUtil;

public class ToolActionRecipeCategory implements IRecipeCategory<ToolActionRecipe> {
    private static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/tool_action_jei.png");
    private static final int GUI_START_X = 0;
    private static final int GUI_START_Y = 0;
    private static final int GUI_WIDTH = 100 - GUI_START_X;
    private static final int GUI_HEIGHT = 41 - GUI_START_Y;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public ToolActionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(TEXTURE, GUI_START_X, GUI_START_Y, GUI_WIDTH, GUI_HEIGHT);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SgBlocks.STONE_ANVIL));
        localizedName = TextUtil.translate("jei", "category.toolAction");
    }

    @Override
    public RecipeType<ToolActionRecipe> getRecipeType() {
        return SGearJeiPlugin.TOOL_ACTION_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, ToolActionRecipe recipe, IFocusGroup focus) {
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .addIngredients(recipe.getTool());
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 3)
                .addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.CATALYST, 22, 22)
                .addIngredients(Ingredient.of(SgBlocks.STONE_ANVIL.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 12)
                .addIngredients(Ingredient.of(recipe.getResult()));
    }
}

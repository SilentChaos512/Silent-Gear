package net.silentchaos512.gear.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.util.Size2i;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.init.ModItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GearCraftingRecipeCategoryJei implements IRecipeCategory<ICraftingRecipe> {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 132;
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public GearCraftingRecipeCategoryJei(IGuiHelper guiHelper) {
        ResourceLocation location = SilentGear.getId("textures/gui/gear_crafting_jei.png");
        this.background = guiHelper.createDrawable(location, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.BLUEPRINT_PACKAGE));
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

    @Override
    public void draw(ICraftingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Collection<ITextComponent> lines = new ArrayList<>();

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient instanceof IGearIngredient) {
                ITextComponent text = ((IGearIngredient) ingredient).getJeiHint().orElse(null);
                if (text != null) {
                    String prefix = (i + 1) + ": ";
                    lines.add(new StringTextComponent(prefix).append(text));
                }
            }
        }

        matrixStack.push();
        float scale = lines.size() > 5 ? 0.75f : 1f;
        matrixStack.scale(scale, scale, 1f);

        FontRenderer font = Minecraft.getInstance().fontRenderer;
        int y = (int) (56 / scale);

        for (ITextComponent line : lines) {
            font.func_238407_a_(matrixStack, line.func_241878_f(), 0, y, -1);
            y += 10;
        }

        matrixStack.pop();
    }
}

/*
package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.crafting.recipe.CraftingRecipeExtension;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GearCraftingRecipeCategoryJei implements IRecipeCategory<CraftingRecipeExtension> {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 132;
    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public GearCraftingRecipeCategoryJei(IGuiHelper guiHelper) {
        Identifier location = SilentGear.getId("textures/gui/gear_crafting_jei.png");
        this.background = guiHelper.createDrawable(location, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, SgItems.BLUEPRINT_PACKAGE.toStack());
        this.localizedName = TextUtil.translate("jei", "group.gearCrafting");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IRecipeType<CraftingRecipeExtension> getRecipeType() {
        return SGearJeiPlugin.GEAR_CRAFTING_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CraftingRecipeExtension recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addIngredients(VanillaTypes.ITEM_STACK, Collections.singletonList(recipe.getResultForDisplay()));

        var ingredients = recipe.getIngredientsForDisplay();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = x + y * 3;
                IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
                if (index < ingredients.size()) {
                    slotBuilder.add(ingredients.get(index));
                }
            }
        }
    }

    @Override
    public void draw(CraftingRecipeExtension recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        Collection<Component> lines = new ArrayList<>();

        var ingredients = recipe.getIngredientsForDisplay();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient.getCustomIngredient() instanceof IGearIngredient gearIngredient) {
                Component text = gearIngredient.getJeiHint().orElse(null);
                if (text != null) {
                    String prefix = (i + 1) + ": ";
                    lines.add(Component.literal(prefix).append(text));
                }
            }
        }

        Matrix3x2fStack matrix = guiGraphics.pose();
        matrix.pushMatrix();
        float scale = lines.size() > 5 ? 0.75f : 1f;
        matrix.scale(scale, scale, matrix);

        Font font = Minecraft.getInstance().font;
        int y = (int) (56 / scale);

        for (Component line : lines) {
            guiGraphics.drawString(font, line.getVisualOrderText(), 0, y, -1, true);
            y += 10;
        }

        matrix.popMatrix();
    }
}
*/

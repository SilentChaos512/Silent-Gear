package net.silentchaos512.gear.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.TextUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GearCraftingRecipeCategoryJei implements IRecipeCategory<CraftingRecipe> {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 132;
    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public GearCraftingRecipeCategoryJei(IGuiHelper guiHelper) {
        ResourceLocation location = SilentGear.getId("textures/gui/gear_crafting_jei.png");
        this.background = guiHelper.createDrawable(location, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.BLUEPRINT_PACKAGE));
        this.localizedName = TextUtil.translate("gui", "category.gearCrafting");
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType() {
        return SGearJeiPlugin.GEAR_CRAFTING_TYPE;
    }

    @Override
    public Component getTitle() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, CraftingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addIngredients(VanillaTypes.ITEM_STACK, Collections.singletonList(recipe.getResultItem()));

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = x + y * 3;
                IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
                if (index < recipe.getIngredients().size()) {
                    slotBuilder.addIngredients(recipe.getIngredients().get(index));
                }
            }
        }
    }

    @Override
    public void draw(CraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Collection<Component> lines = new ArrayList<>();

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient instanceof IGearIngredient) {
                Component text = ((IGearIngredient) ingredient).getJeiHint().orElse(null);
                if (text != null) {
                    String prefix = (i + 1) + ": ";
                    lines.add(Component.literal(prefix).append(text));
                }
            }
        }

        stack.pushPose();
        float scale = lines.size() > 5 ? 0.75f : 1f;
        stack.scale(scale, scale, 1f);

        Font font = Minecraft.getInstance().font;
        int y = (int) (56 / scale);

        for (Component line : lines) {
            font.drawShadow(stack, line.getVisualOrderText(), 0, y, -1);
            y += 10;
        }

        stack.popPose();
    }
}

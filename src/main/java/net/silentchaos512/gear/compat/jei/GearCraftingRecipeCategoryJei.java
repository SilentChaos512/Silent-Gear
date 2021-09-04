package net.silentchaos512.gear.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.util.Size2i;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GearCraftingRecipeCategoryJei implements IRecipeCategory<CraftingRecipe> {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 132;
    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public GearCraftingRecipeCategoryJei(IGuiHelper guiHelper) {
        ResourceLocation location = SilentGear.getId("textures/gui/gear_crafting_jei.png");
        this.background = guiHelper.createDrawable(location, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.BLUEPRINT_PACKAGE));
        this.localizedName = TextUtil.translate("gui", "category.gearCrafting");
        this.craftingGridHelper = guiHelper.createCraftingGridHelper(1);
    }

    @Override
    public ResourceLocation getUid() {
        return SGearJeiPlugin.GEAR_CRAFTING;
    }

    @Override
    public Class<? extends CraftingRecipe> getRecipeClass() {
        return CraftingRecipe.class;
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
    public void setRecipe(IRecipeLayout recipeLayout, CraftingRecipe recipe, IIngredients ingredients) {
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

        if (ret.isEmpty() || !Config.Common.allowLegacyMaterialMixing.get()) {
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
    public void setIngredients(CraftingRecipe recipe, IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
        ingredients.setInputIngredients(recipe.getIngredients());
    }

    @Nullable
    private static Size2i getRecipeSize(CraftingRecipe recipe) {
        if (recipe instanceof IShapedRecipe) {
            IShapedRecipe shapedRecipe = (IShapedRecipe) recipe;
            return new Size2i(shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight());
        } else {
            return null;
        }
    }

    @Override
    public void draw(CraftingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        Collection<Component> lines = new ArrayList<>();

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient instanceof IGearIngredient) {
                Component text = ((IGearIngredient) ingredient).getJeiHint().orElse(null);
                if (text != null) {
                    String prefix = (i + 1) + ": ";
                    lines.add(new TextComponent(prefix).append(text));
                }
            }
        }

        matrixStack.pushPose();
        float scale = lines.size() > 5 ? 0.75f : 1f;
        matrixStack.scale(scale, scale, 1f);

        Font font = Minecraft.getInstance().font;
        int y = (int) (56 / scale);

        for (Component line : lines) {
            font.drawShadow(matrixStack, line.getVisualOrderText(), 0, y, -1);
            y += 10;
        }

        matrixStack.popPose();
    }
}

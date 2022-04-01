package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.crafting.recipe.ExtendedSingleItemRecipe;

public class PressingRecipe extends ExtendedSingleItemRecipe {
    public static final RecipeType<PressingRecipe> PRESSING_TYPE = ModRecipes.registerType(Const.PRESSING);

    public PressingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result) {
        super(PRESSING_TYPE, ModRecipes.PRESSING.get(), id, "", ingredient, result);
    }
}
